(define (domain trafficJam)
    (:requirements :strips)
    (:predicates 
        (SQUARE ?s)
        (VEHICLE_2H ?v)
        (VEHICLE_2V ?v)
        (VEHICLE_3H ?v)
        (VEHICLE_3V ?v)
        (next_to_up ?s1 ?s2)
        (next_to_right ?s1 ?s2)
        (empty ?s)
        (at_2 ?v ?s1 ?s2)
        (at_3 ?v ?s1 ?s2 ?s3))

    (:action MoveRight2H
        :parameters (?v ?s1 ?s2 ?d)
        :precondition (and (VEHICLE_2H ?v) (at_2 ?v ?s1 ?s2) (empty ?d)
         (next_to_right ?s2 ?d))
        :effect (and (empty ?s1) (at_2 ?v ?s2 ?d)
         (not (empty ?d)) (not (at_2 ?v ?s1 ?s2)))
    )

    (:action MoveLeft2H
     :parameters (?v ?s1 ?s2 ?d)
     :precondition (and (VEHICLE_2H ?v) (at_2 ?v ?s1 ?s2) (empty ?d)
      (next_to_right ?d ?s1))
     :effect (and (empty ?s2) (at_2 ?v ?d ?s1)
      (not (empty ?d)) (not (at_2 ?v ?s1 ?s2)))
    )

    (:action MoveRight3H
     :parameters (?v ?s1 ?s2 ?s3 ?d)
     :precondition (and (VEHICLE_3H ?v) (at_3 ?v ?s1 ?s2 ?s3) (empty ?d)
      (next_to_right ?s3 ?d))
     :effect (and (empty ?s1) (at_3 ?v ?s2 ?s3 ?d)
      (not (empty ?d)) (not (at_3 ?v ?s1 ?s2 ?s3))))

    (:action MoveLeft3H
     :parameters (?v ?s1 ?s2 ?s3 ?d)
     :precondition (and (VEHICLE_3H ?v) (at_3 ?v ?s1 ?s2 ?s3) (empty ?d)
      (next_to_right ?d ?s1))
     :effect (and (empty ?s3) (at_3 ?v ?d ?s1 ?s2)
      (not (empty ?d)) (not (at_3 ?v ?s1 ?s2 ?s3))))

    (:action MoveUp2V
     :parameters (?v ?s1 ?s2 ?d)
     :precondition (and (VEHICLE_2V ?v) (at_2 ?v ?s1 ?s2) (empty ?d)
      (next_to_up ?s1 ?d))
     :effect (and (empty ?s2) (at_2 ?v ?d ?s1)
      (not (empty ?d)) (not (at_2 ?v ?s1 ?s2))))

    (:action MoveDown2V
     :parameters (?v ?s1 ?s2 ?d)
     :precondition (and (VEHICLE_2V ?v) (at_2 ?v ?s1 ?s2) (empty ?d)
      (next_to_up ?d ?s2))
     :effect (and (empty ?s1) (at_2 ?v ?s2 ?d)
      (not (empty ?d)) (not (at_2 ?v ?s1 ?s2))))

    (:action MoveUp3V
     :parameters (?v ?s1 ?s2 ?s3 ?d)
     :precondition (and (VEHICLE_3V ?v) (at_3 ?v ?s1 ?s2 ?s3) (empty ?d)
      (next_to_up ?s1 ?d))
     :effect (and (empty ?s3) (at_3 ?v ?d ?s1 ?s2)
      (not (empty ?d)) (not (at_3 ?v ?s1 ?s2 ?s3))))

    (:action MoveDown3V
     :parameters (?v ?s1 ?s2 ?s3 ?d)
     :precondition (and (VEHICLE_3V ?v) (at_3 ?v ?s1 ?s2 ?s3) (empty ?d)
      (next_to_up ?d ?s3))
     :effect (and (empty ?s3) (at_3 ?v ?s2 ?s3 ?d)
      (not (empty ?d)) (not (at_3 ?v ?s1 ?s2 ?s3))))
)

