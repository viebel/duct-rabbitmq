(ns duct.amqp.rabbitmq.langohr
  (:require [integrant.core :as ig]
            [langohr.core :as rmq]))

(defmethod ig/init-key :duct.amqp.rabbitmq/langohr [_ settings]
  (rmq/connect settings))


(defmethod ig/halt-key! :duct.amqp.rabbitmq/langohr [_ conn]
  (when-not (rmq/closed? conn)
    (rmq/close conn)))

(defmethod ig/suspend-key! :duct.amqp.rabbitmq/langohr [_ _])
