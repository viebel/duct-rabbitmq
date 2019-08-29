(defproject viebel/duct-rabbitmq "0.1.2"
  :description "Integrant methods for connecting to RabbitMq via Langohr"
  :url "https://github.com/agrison/duct-mongodb"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :lein-release {:scm :git :deploy-via :clojars}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [integrant "0.6.3"]
                 [com.novemberain/langohr "5.1.0"]])
