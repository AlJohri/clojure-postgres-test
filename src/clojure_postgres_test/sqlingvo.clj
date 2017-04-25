(ns clojure-postgres-test.sqlingvo
  (:gen-class)
  (:refer-clojure :exclude [distinct group-by update])
  (:require [clojure.string :as str]
            [sqlingvo.core :refer [db sql as select where from limit offset]]
            [sqlingvo.util :refer [sql-name-underscore sql-keyword-hyphenate]]))

; https://github.com/r0man/sqlingvo#syntax-quoting
; Syntax quoting
; When using SQLingvo to build parameterized SQL statements,
; you often want to use the parameters in a SQL expression.
; This can be accomplished with syntax quoting. Note the back
; tick character in the where clause.
;
; (defn films-by-kind [db kind]
;   (select db [:id :name]
;     (from :films)
;     (where `(= :kind ~kind))))

(def my-db (db :postgresql {:sql-name sql-name-underscore}))

(defn get-articles-querymap [lim off fields]
  (select my-db (map #(as `(-> :original ~(clojure.core/name %)) %) fields)
    (from (as :articles :a))
    (limit lim)
    (offset off)))

(defn get-articles-from-topic-querymap [topic-id lim off fields]
  (select my-db (map #(as `(-> :original ~(clojure.core/name %)) %) fields)
        (from (as :articles :a) (as :article-topics :at))
        (where `(and
                  (= :a.id :at.article-id)
                  (= :at.topic-id ~topic-id)
                  (is-not-null :at.ensemble_score nil)))
        (limit lim)
        (offset off)))

(def get-articles-query (comp sql get-articles-querymap))
(def get-articles-from-topic-query (comp sql get-articles-from-topic-querymap))
