(ns clojure-postgres-test.core
  (:gen-class)
  (:require [jdbc.core :as jdbc]
            [clojure-postgres-test.jdbc]
            [clojure-postgres-test.honeysql :as h]
            [clojure-postgres-test.sqlingvo :as s]))

(def dbspec (System/getenv "POSTGRES_URL"))
(def fields [:_id :date :analyzed-date :meta :topics])

(defn execute-query-and-print-result [conn sql]
  (let [result (jdbc/fetch conn sql)]
        (doseq [row result]
          (println row)
          (println))))

(defn print-query-and-execute [query]
  (println "Query:")
  (println query)
  (println)
  (with-open [conn (jdbc/connection dbspec)]
    (execute-query-and-print-result conn query)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]

  (println "honeysql")
  (print-query-and-execute (h/get-articles-query 1 0 fields))
  (print-query-and-execute (h/get-articles-from-topic-query "539b36d6e4b05b66e1c47661" 1 0 fields))
  (println)

  (println "sqlingvo")
  (print-query-and-execute (s/get-articles-query 1 0 fields))
  (print-query-and-execute (s/get-articles-from-topic-query "539b36d6e4b05b66e1c47661" 1 0 fields))
  (println)

  (println "Done!"))
