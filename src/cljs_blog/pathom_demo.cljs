(ns cljs-blog.pathom-demo
  (:require [com.wsscode.pathom3.connect.indexes :as pci]
            [com.wsscode.pathom3.connect.operation :as pco]
            [com.wsscode.pathom3.interface.smart-map :as psm]
            [com.wsscode.pathom3.interface.eql :as p.eql]))

(pco/defresolver package-id->order-id
  [{:keys [package-id]}]
  {:order-id "oid"})

(pco/defresolver package-id->info
  [{:keys [package-id]}]
  ;; 一个 input, 对应一个 output, 但是 output 有多个字段
  {::pco/output [:create-time
                 :status
                 :pick-time
                 :weight]}
  {:create-time "2022"
   :status "send"
   :pick-time "2022-10-01"
   :weight "1kg"})

(pco/defresolver package-id->package-details
  [{:keys [package-id]}]
  ;; output 的 shape 和返回值是完全一样的
  {::pco/output [{:package-details [:sku :num :price]}]}
  {:package-details [{:sku-code "sku1"
                      :num 1
                      :price 10}
                     {:sku-code "sku2"
                      :num 2
                      :price 15}]})

(pco/defresolver sku-code->sku
  [{:keys [:sku-code]}]
  {::pco/output [:sku-id
                 :primary-image
                 :cost]}
  {:sku-id 1
   :primary-image "image"
   :cost 10})

(pco/defresolver package-total-price
  [{:keys [package-details]}]
  ;; input 也是可以指定 shape, 方式和 output 指定 shape 一样
  ;; 就可以做到把多个值变回一个值
  {::pco/input [{:package-details [:price]}]}
  {:package-total-price (transduce (map :price) + 0 package-details)})

(def env
  (pci/register [package-id->order-id
                 package-id->info
                 package-id->package-details
                 sku-code->sku
                 package-total-price]))

(def sm (psm/smart-map env {:package-id 1}))

(sm :package-total-price)

(p.eql/process env
               ;; 已知信息
               {:package-id 1}
               ;; 需要查询的 shape
               [:order-id 
                ;; 可以直接查 package-details
                ;; :package-details
                ;; 也可以只要里面的部分字段, 不能两个共存, 要注释掉一个
                {:package-details [:price
                                   :cost
                                   ;; '* 会把这一层的字段全部显示出来
                                   '*]}])