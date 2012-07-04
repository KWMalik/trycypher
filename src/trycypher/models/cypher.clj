(ns trycypher.models.cypher
  (:require 
		[clojurewerkz.neocons.rest.records :as records]
		[clojurewerkz.neocons.rest.cypher :as cy]
))

(defn cypher-convert-value [value] 
(if (:type value) (records/instantiate-rel-from value) 
    (if (:length value) (records/instantiate-path-from value)
        (if (:self value) (records/instantiate-node-from value) 
            value)))
)

(defn cypher-convert-cell [[col value]] 
    [col (render (cypher-convert-value value))])

(defn cypher-convert-row [row] (map #(render (cypher-convert-value %)) row))

(defn cypher-convert-result [row] (map cypher-convert-cell row))

(defn pad 
    ([row sep nl] (str (apply str (map #(format "%10s%s" % sep) row)) nl))
    ([row] (pad row "|" "\n"))
)

(defn render [value]
   (if (coll? value) (doall (flatten (seq value))) value)
)


(defn result-to-table [{data :data columns :columns}]
    (str (pad columns) (apply str (map pad (map cypher-convert-row data)))))

(defn eval-cypher [expr]
  (try
    {:expr expr :result (result-to-table (cy/query expr))}
    (catch Exception e
      {:error true :message (str (.getMessage e))})))

; test

;(ns neocons.docs.examples                                               
;  (:require [clojurewerkz.neocons.rest :as nr]
;            [clojurewerkz.neocons.rest.nodes :as nn]
;            [clojurewerkz.neocons.rest.relationships :as nrl]
;            [clojurewerkz.neocons.rest.cypher :as cy]))

;(nr/connect! "http://localhost:7474/db/data/")
;(def res (cy/tquery "start n=node(*) match p=n-[r]->() return n,r,p,n.name?,type(r) as t,length(p) as l limit 10"))
;(map cypher-convert-result res)