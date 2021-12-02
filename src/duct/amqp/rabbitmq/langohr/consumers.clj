(ns duct.amqp.rabbitmq.langohr.consumers
  "Duct multimethods for langohr consumers"
  (:require [integrant.core :as ig]
            [duct.logger :as logger]
            [langohr.core :as rmq]
            [langohr.queue :as lq]
            [langohr.channel :as lch]
            [langohr.consumers :as lc]))

(defn foo
  "This function is here to allow navigation to namespce"
  [])


(defn- start-consumers [connection logger consumers]
  (doall (for [{:keys [queue handler declare-queue exchange]} consumers]
           (let [ch (lch/open connection)
                 queue (if declare-queue
                         (lq/declare ch queue declare-queue)
                         queue)]
             (when exchange
               (lq/bind ch (:queue queue) exchange))
             (lc/subscribe ch queue handler {})
             (when @logger
               (logger/log @logger :report ::starting-consumer {:queue queue
                                                                :declare-queue declare-queue}))
             {:ch ch
              :queue queue}))))

(defmethod ig/init-key :duct.amqp.rabbitmq.langohr/consumers [_ {:keys [connection-settings connection consumers logger]}]
  (let  [logger (atom logger)
         connection (or connection (rmq/connect connection-settings))]
    {:logger logger
     :channels (start-consumers connection logger consumers)}))

(defmethod ig/halt-key! :duct.amqp.rabbitmq.langohr/consumers [_ {:keys [channels logger]}]
  (doseq [{:keys [ch queue]} channels]
    (when @logger
      (logger/log @logger :report ::stopping-consumer {:queue queue}))
    (when-not (rmq/closed? ch )
      (rmq/close ch ))))
