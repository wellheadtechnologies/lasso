(ns com.wellhead.lasso.ClojureReader
  (:import (java.io PushbackReader BufferedReader FileReader InputStreamReader)
	   (com.wellhead.lasso WHLasFile Curve Header WHVersionHeader LasFile
			       WHWellHeader WHCurveHeader WHParameterHeader
			       WHDescriptor WHCurve WHHeader)
	   (java.util List ArrayList))
  (:gen-class
   :implements [com.wellhead.lasso.LasReader]
   :methods [[parseName [Object] String]
	     [parseHeaders [Object] java.util.List]
	     [parseCurves [Object] java.util.List]
	     [parseCurve [Object] com.wellhead.lasso.Curve]
	     [parseDescriptors [Object] java.util.List]
	     [parseDescriptor [Object] com.wellhead.lasso.Descriptor]
	     [parseLasData [Object] java.util.List]
	     [parseLasFile [Object] com.wellhead.lasso.LasFile]
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
	lf (read reader)]
    (.parseLasFile this lf)))

(defn -readCurve [this path]
  (let [reader 
	(new PushbackReader
	     (new BufferedReader
		  (if (= path "stdin")
		    (new InputStreamReader System/in)
		    (new FileReader path))))
	curve (read reader)]
    (.parseCurve this curve)))

(defn -parseLasFile [this lf]
  (let [whlasfile (new WHLasFile)
	whcurves (.parseCurves this lf)
	whindex (first whcurves)
	whcurves (rest whcurves)]    
    (doseq [whcurve whcurves]
      (.setIndex whcurve whindex))
    (doto whlasfile
      (.setName (.parseName this lf))
      (.setHeaders (.parseHeaders this lf))
      (.setIndex whindex)
      (.setCurves whcurves))))

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
	list (new ArrayList (count curves))]
    (doseq [curve curves]
      (.add list (.parseCurve this curve)))
    list))

(defn -parseCurve [this curve]
  (let [whcurve (new WHCurve)]
    (doto whcurve
      (.setDescriptor (.parseDescriptor this (:descriptor curve)))
      (.setLasData (.parseLasData this curve)))))

(defn -parseLasData [this curve]
  (let [data (:data curve)
	list (new ArrayList (count data))]
    (doseq [d data]
      (.add list d))
    list))