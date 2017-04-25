(ns clojure-postgres-test.core
  (:gen-class)
  (:require [honeysql.core :as sql]
            [honeysql.helpers :refer [select where join from limit offset]]
            [jdbc.core :as jdbc]
            [clojure-postgres-test.postgres]))

(defn select-json-key [parent k]
  (-> k
      (clojure.core/name)
      (#(vector (sql/raw (str (clojure.core/name parent) "->'" % "'")) %))))

(defn select-json-keys [parent keyz]
  (->> keyz
       (map (partial select-json-key parent))))

(defn get-articles-query [lim off fields]
  (-> (apply select (select-json-keys :a.original fields))
      (from [:articles :a])
      (limit lim)
      (offset off)))

(defn get-articles-from-topic-query [topic-id lim off fields]
  (-> (apply select (select-json-keys :a.original fields))
      (from [:articles :a] [:article-topics :at])
      (where [:and
        [:= :a.id :at.article-id]
        [:= :at.topic-id topic-id]
        [:not= :at.ensemble_score nil]])
      (limit lim)
      (offset off)))

(defn execute-query-and-print [conn sql]
  (let [result (jdbc/fetch conn sql)]
        (doseq [row result]
          (println row)
          (println))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (def dbspec (System/getenv "POSTGRES_URL"))
  (def fields [:_id :date :analyzed-date :meta :topics])

  (def sqlmap1 (get-articles-query 2 0 fields))
  (def sql1 (sql/format sqlmap1 :quoting :ansi))
  (println "Query1:")
  (println sql1)
  (println)

  (with-open [conn (jdbc/connection dbspec)]
    (execute-query-and-print conn sql1))

  (def sqlmap2 (get-articles-from-topic-query "539b36d6e4b05b66e1c47661" 2 0 fields))
  (def sql2 (sql/format sqlmap2 :quoting :ansi))
  (println "Query2:")
  (println sql2)
  (println)

  (with-open [conn (jdbc/connection dbspec)]
    (execute-query-and-print conn sql2))

  (println "Done!"))
