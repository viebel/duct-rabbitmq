(ns duct.amqp.rabbitmq.langohr-consumers
  (:require [integrant.core :as ig]
            [langohr.core :as rmq]
            [langohr.queue :as lq]
            [langohr.channel :as lch]
            [langohr.consumers :as lc]))

(defmethod ig/init-key :duct.amqp.rabbitmq/langohr-consumers [_ {:keys [connection-settings connection consumers]}]
  (let [connection (or connection (rmq/connect connection-settings))]
    (doall (for [{:keys [queue handler declare-queue ]} consumers]
             (let [ch (lch/open connection)]
               (when declare-queue
                 (lq/declare ch queue declare-queue))
               (lc/subscribe ch queue handler {})
               ch)))))

(defmethod ig/halt-key! :duct.amqp.rabbitmq/langohr-consumers [_ channels]
  (doseq [ch channels]
    (when-not (rmq/closed? ch)
      (rmq/close ch))))
