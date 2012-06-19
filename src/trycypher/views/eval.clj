(ns trycypher.views.eval
  (:require [noir.core :refer [defpage]]
            [trycypher.models.cypher :refer [eval-cypher]]
            [noir.response :as resp]))

(defpage "/eval.json" {:keys [expr jsonp]}
  (let [{:keys [expr result error message] :as res} (eval-cypher expr)
        data (if error
               res
                 {:expr expr :result result})]
    
    (if jsonp
      (resp/jsonp jsonp data)
      (resp/json data))))