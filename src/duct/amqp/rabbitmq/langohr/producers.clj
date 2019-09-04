(ns duct.amqp.rabbitmq.langohr.producers
  (:require [integrant.core :as ig]
            [langohr.core :as rmq]
            [langohr.basic :as lb]
            [langohr.channel :as lch]))

(defmethod ig/init-key :duct.amqp.rabbitmq.langohr/producers [_ {:keys [connection-settings connection producers]}]
  (let [connection (or connection (rmq/connect connection-settings))]
    (->> (map (fn [{:keys [name exchange routing-key] :or {exchange ""}}]
                (let [ch (lch/open connection)]
                  [name {:ch ch
                         :publish-fn (partial lb/publish ch exchange routing-key)}]))
              producers)
         (into {}))))

(defmethod ig/halt-key! :duct.amqp.rabbitmq.langohr/producers [_ producers-map]
  (doseq [{:keys [ch]} (vals producers-map)]
    (when-not (rmq/closed? ch)
      (rmq/close ch))))
