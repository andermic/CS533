(define (domain trafficJam)
    (:predicates
        (SQUARE ?s)
        (VEHICLE-2H ?v)
        (VEHICLE-2V ?v)
        (VEHICLE-3H ?v)
        (VEHICLE-3V ?v)
        (next-to-up ?s1 ?s2)
        (next-to-right ?s1 ?s2)
        (empty ?s)
        (at-2 ?v ?s1 ?s2)
        (at-3 ?v ?s1 ?s2 ?s3)
    )
   
    (:action MoveRight2H
        :parameters (?v ?s1 ?s2 ?d)
        :precondition (and (VEHICLE-2H ?v) (at-2 ?v ?s1 ?s2) (empty ?s) (next-to-right ?s2 ?d))
        :effect (and (empty ?s1) (at-2 ?v ?s2 ?d) (not (empty ?d)) (not (at-2 ?v ?s1 ?s2)))
    )
)

