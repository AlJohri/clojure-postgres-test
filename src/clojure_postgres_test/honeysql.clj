(ns clojure-postgres-test.honeysql
  (:gen-class)
  (:require [honeysql.core :as sql]
            [honeysql.helpers :refer [select where join from limit offset]]))

(defn make-sql [querymap] (sql/format querymap :quoting :ansi))

(defn select-json-key [parent k]
  (-> k
      (clojure.core/name)
      (#(vector (sql/raw (str (clojure.core/name parent) "->'" % "'")) %))))

(defn select-json-keys [parent keyz]
  (->> keyz
       (map (partial select-json-key parent))))

(defn get-articles-querymap [lim off fields]
  (-> (apply select (select-json-keys :a.original fields))
      (from [:articles :a])
      (limit lim)
      (offset off)))

(defn get-articles-from-topic-querymap [topic-id lim off fields]
  (-> (apply select (select-json-keys :a.original fields))
      (from [:articles :a] [:article-topics :at])
      (where [:and
        [:= :a.id :at.article-id]
        [:= :at.topic-id topic-id]
        [:not= :at.ensemble_score nil]])
      (limit lim)
      (offset off)))

(def get-articles-query (comp make-sql get-articles-querymap))
(def get-articles-from-topic-query (comp make-sql get-articles-from-topic-querymap))

