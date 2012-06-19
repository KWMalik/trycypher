(ns trycypher.server
  (:require [noir.server :as server]
            [ring.middleware.file :refer [wrap-file]]
            [clojurewerkz.neocons.rest :as nr]
))

(def neo4j-url (or (System/getenv "NEO4J_REST_URL") "http://localhost:7474/db/data/"))

(nr/connect! neo4j-url)

(server/add-middleware wrap-file (System/getProperty "user.dir"))
(server/load-views "src/trycypher/views")

(defn to-port [s]
  (when-let [port s] (Long. port)))

(defn tryclj [& [port]]
  (server/start
   (or (to-port port)
       (to-port (System/getenv "PORT")) ;; For deploying to Heroku
       8801)
   {:session-cookie-attrs {:max-age 600}}))

(defn -main [& args] (tryclj (first args)))