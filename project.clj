(defproject trycypher "0.1.0-SNAPSHOT"
  :description "A simple web-based Cypher REPL for trying out Neo4j without having to install it."
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [noir "1.3.0-beta3"]
                 [commons-lang/commons-lang "2.5"]
                 [clojurewerkz/neocons "1.0.0-rc1"]
                 ]
  :jvm-opts ["-Djava.security.policy=example.policy""-Xmx80M"]
  :main trycypher.server)


