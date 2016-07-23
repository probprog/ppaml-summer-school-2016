(query one-flip [outcome]
 (let [theta (sample (beta 1 1))]
  (observe (flip theta) outcome)
  (predict theta)))

(fn one-flip [outcome state cont]
 (sample (beta 1 1) 
  state 
  (fn [theta state] 
   (observe (flip theta) outcome
    state
    (fn [_ state]
     (cont nil
      (add-predict 
       state bias)))))))

(fn [outcome $state]
 (->sample 'S24726
  (beta 1 1)
  (fn [theta $state]
   (->observe 'O24724
    (flip theta) 
    outcome
    (fn [_ $state] 
     (->result theta $state))
    $state))
  $state))

{:id 'S24726
 :dist (beta 1 1)
 :cont (fn [theta $state]
         ...)
 :state $state}

{:id 'O24724
 :dist (flip theta) 
 :value outcome
 :cont (fn [_ $state] 
         (->result theta $state))
 :state $state}

{:result theta 
 :state $state}

{:result theta
 :log-weight 
   (:log-weight $state)
 :predicts 
   (:predicts $state)}