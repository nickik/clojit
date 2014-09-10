(ns clojit.bytecode-fn
  (:require
    [clojure.pprint :as p]
    [clojure.tools.trace :as t]))

(declare find-constant-index bc-gen)

(defmacro dbg[x] `(let [x# ~x] (println '~x "=" x#) x#))

(def empty-constant-table
                     {:CSTR []
                      :CKEY []
                      :CINT []
                      :CFLOAT []
                      :CFUNC {}})


(def constant-table (ref empty-constant-table))

(defn bc-gen [inst a-slot b-slot c-slot]
  {:op inst
   :a a-slot
   :b b-slot
   :c c-slot})

(defn ADDVV [a-slot b-slot c-slot]
  (bc-gen :ADDVV a-slot b-slot c-slot))

(defn SUBVV [a-slot b-slot c-slot]
  (bc-gen :SUBVV a-slot b-slot c-slot))

(defn MULVV [a-slot b-slot c-slot]
  (bc-gen :MULVV a-slot b-slot c-slot))

(defn DIVVV [a-slot b-slot c-slot]
  (bc-gen :DIVVV a-slot b-slot c-slot))

(defn MODVV [a-slot b-slot c-slot]
  (bc-gen :MODVV a-slot b-slot c-slot))

(defn POWVV [a-slot b-slot c-slot]
  (bc-gen :POWVV a-slot b-slot c-slot))

(defn ISLT [a-slot b-slot c-slot]
  (bc-gen :ISLT a-slot b-slot c-slot))

(defn ISGE [a-slot b-slot c-slot]
  (bc-gen :ISGE a-slot b-slot c-slot))

(defn ISLE [a-slot b-slot c-slot]
  (bc-gen :ISLE a-slot b-slot c-slot))

(defn ISGT [a-slot b-slot c-slot]
  (bc-gen :ISGT a-slot b-slot c-slot))

(defn ISEQ [a-slot b-slot c-slot]
  (bc-gen :ISEQ a-slot b-slot c-slot))

(defn ISNEQ [a-slot b-slot c-slot]
  (bc-gen :ISNEQ a-slot b-slot c-slot))

(defn JUMPF [a-slot d-slot]
  {:op :JUMPF
   :a a-slot
   :d d-slot})

(defn JUMP [d-slot]
  {:op :JUMP
   :a nil
   :d d-slot})

(defn CALL [a-slot lit]
  [{:op :CALL
    :a a-slot
    :d lit}])

(defn MOV [a-slot d-slot]
  {:op :MOV
   :a a-slot
   :d d-slot})

(defn NOT [a-slot d-slot]
  {:op :NOT
   :a a-slot
   :d d-slot})

(defn NEG [a-slot d-slot]
  {:op :NEG
   :a a-slot
   :d d-slot})

(defn NSGETS [a-slot d-slot-str]
   [{:op :NSGETS
     :a a-slot
     :d d-slot-str}])

(defn constant-table-bytecode [bytecode a-slot const]
  {:op bytecode
   :a a-slot
   :d (find-constant-index bytecode const)})

(defn bool-bytecode [a-slot const]
  {:op :CBOOL
   :a a-slot
   :d (if const 1 0)})

(defn NSSETS [a-slot d-slot]
  {:op :NSSETS
   :a a-slot
   :d d-slot})

(defn FUNCF [a-slot-arg-count]
  {:op :FUNCF
   :a a-slot-arg-count
   :d nil})

(defn CFUNC [a-slot d-slot]
  {:op :CFUNC
   :a a-slot
   :d d-slot})

(defn CNIL [a-slot]
  {:op :CNIL
   :a a-slot
   :d nil})

;; ----------------------- CONSTANT TABLE ----------------------------


(defn find-constant-index [op const]
  (first (remove nil? (map-indexed (fn [a b]
                                     (when (= b const)
                                       a))
                                   (op @constant-table)))))


(defn find-fn-index [k]
  (get (:CFUNC @constant-table) k))

(defn put-in-constant-table [op const]
  (if (find-constant-index op const)
    @constant-table
    (dosync
     (alter constant-table assoc op (conj (op @constant-table) const)))))

(defn put-in-function-table [k f]
  (dosync
   (alter constant-table assoc-in [:CFUNC k] f)))

(defn set-empty []
  (dosync (alter constant-table (fn [ct] empty-constant-table))))



