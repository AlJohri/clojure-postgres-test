(ns clojure-postgres-test.postgres
  (:require [jdbc.proto :as proto]
            [cheshire.core :as json])
  (:import [org.postgresql.util PGobject]))

;; ISQLType handles a conversion from user type to jdbc compatible
;; types. In this case we are extending any implementation of clojure
;; IPersistentMap (for convert it to json string).
(extend-protocol proto/ISQLType
  clojure.lang.IPersistentMap

  ;; This method, receives a instance of IPersistentMap and
  ;; active connection, and return jdbc compatible type.
  (as-sql-type [self conn]
    (doto (PGobject.)
      (.setType "json")
      (.setValue (json/generate-string self))))

  ;; This method handles assignation of now converted type
  ;; to jdbc statement instance.
  (set-stmt-parameter! [self conn stmt index]
    (.setObject stmt index (proto/as-sql-type self conn))))

;; ISQLResultSetReadColumn handles the conversion from sql types
;; to user types. In this case, we are extending PGobject for handle
;; json field conversions to clojure hash-map.
(extend-protocol proto/ISQLResultSetReadColumn
  PGobject
  (from-sql-type [pgobj conn metadata i]
    (let [type  (.getType pgobj)
          value (.getValue pgobj)]
      (case type
        "json" (json/parse-string value)
        "jsonb" (json/parse-string value)
        :else value))))
