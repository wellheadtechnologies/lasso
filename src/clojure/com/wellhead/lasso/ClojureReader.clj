(ns com.wellhead.lasso.ClojureReader
  (:import (java.io PushbackReader BufferedReader FileReader InputStreamReader)
	   (com.wellhead.lasso WHLasFile Curve Header WHVersionHeader
			       WHWellHeader WHCurveHeader WHParameterHeader
			       WHDescriptor WHCurve WHHeader)
	   (java.util List ArrayList))
  (:gen-class
   :implements [com.wellhead.lasso.LasReader]
   :methods [[parseName [Object] String]
	     [parseHeaders [Object] java.util.List]
	     [parseCurves [Object Object] java.util.List]
	     [parseCurve [Object Object] com.wellhead.lasso.Curve]
	     [parseDescriptors [Object] java.util.List]
	     [parseDescriptor [Object] com.wellhead.lasso.Descriptor]
	     [parseLasData [Object] java.util.List]
	     ]))


(defn -canRead [this protocol]
  (= protocol "clojure"))

(defn -readLasFile [this path]
  (let [reader 
	(new PushbackReader 
	     (new BufferedReader
		  (if (= path "stdin") 
		    (new InputStreamReader System/in)
		    (new FileReader path))))
	lf (read reader)
	whlasfile (new WHLasFile)]    
    (doto whlasfile
      (.setName (.parseName this lf))
      (.setHeaders (.parseHeaders this lf))
      (.setIndex (.parseCurve this (:index lf)))
      (.setCurves (.parseCurves this lf)))))

(defn -readCurve [this path]
  (let [reader 
	(new PushbackReader
	     (new BufferedReader
		  (if (= path "stdin")
		    (new InputStreamReader System/in)
		    (new FileReader path))))
	curve (read reader)]
    (.parseCurve this)))

(defn -parseName [this lasfile]
  (:name lasfile))

(defn -parseHeaders [this lasfile]
  (let [headers (:headers lasfile)
	list (new ArrayList (count headers))]
    (doseq [header headers]
      (let [#^String htype (:type header)
	    #^WHHeader wheader
	    (cond 
	     (= "VersionHeader" htype)
	     (new WHVersionHeader)
	 
	     (= "WellHeader" htype)
	     (new WHWellHeader)

	     (= "CurveHeader" htype)
	     (new WHCurveHeader)

	     (= "ParameterHeader" htype)
	     (new WHParameterHeader))]
	(doto wheader
	  (.setDescriptors (.parseDescriptors this header)))
	(.add list wheader)))
    list))

(defn -parseDescriptors [this header]
  (let [descriptors (:descriptors header)
	list (new ArrayList (count descriptors))]
    (doseq [descriptor descriptors]
      (.add list (.parseDescriptor this descriptor)))
    list))

(defn -parseDescriptor [this descriptor]
  (let [whd (new WHDescriptor)]
    (doto whd
      (.setMnemonic (:mnemonic descriptor))
      (.setUnit (:unit descriptor))
      (.setData (:data descriptor))
      (.setDescription (:description descriptor)))
    whd))

(defn -parseCurves [this lasfile]
  (let [curves (:curves lasfile)
	list (new ArrayList (count curves))
	index (.parseCurve this nil (:index lasfile))]
    (doseq [curve curves]
      (.add list (.parseCurve this curve)))
    list))

(defn -parseCurve [this index curve]
  (let [whcurve (new WHCurve)]
    (doto whcurve
      (.setDescriptor (.parseDescriptor this (:descriptor curve)))
      (.setIndex index)
      (.setLasData (.parseLasData this curve)))))