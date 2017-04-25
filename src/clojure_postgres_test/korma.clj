; https://github.com/korma/Korma

(ns clojure-postgres-test.korma
  (:gen-class)
  (:require [korma.db :refer [defdb postgres]]
            [korma.core :refer [sql-only raw select select* where order limit offset
                                fields has-one with]]))

; ["original->'meta'->'headline'" :headline]
; (map #(str "original->'" (clojure.core/name %) "'") fieldz)

; https://coderwall.com/p/omnlba/workaround-korma-fields-variadic-arguments
(defmacro only-fields
    [query fields]
    `(let [field-list# (list* ~fields)
             fargs# (cons ~query field-list#)]
        (apply korma.core/fields fargs#)))

(defn get-articles-query [lim off fieldz]
  (sql-only (select "articles"
      (only-fields (map #(vector (str "original->'" (clojure.core/name %) "'") %) fieldz))
      (limit lim)
      (offset off))))

; TODO
; (defn get-articles-from-topic-query [topic-id lim off fieldz]
;   (sql-only (select "articles" "article_topics"
;       (only-fields (map #(vector (str "original->'" (clojure.core/name %) "'") %) fieldz))
;       (limit lim)
;       (offset off))))
