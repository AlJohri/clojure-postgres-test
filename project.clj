(defproject clojure-postgres-test "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
    [org.clojure/clojure "1.8.0"]
    [cheshire "5.7.1"]
    [org.postgresql/postgresql "42.0.0"]
    [funcool/clojure.jdbc "0.9.0"]
    [korma "0.4.3"]
    [honeysql "0.8.2"]
    [sqlingvo "0.9.4-SNAPSHOT"]]
  :main ^:skip-aot clojure-postgres-test.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
