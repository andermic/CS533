(define (domain blockworld)
(:predicates (clear ?a)
    (clawEmpty)
    (holding ?a)
    (on ?a ?b)
)

(:action Pickup
    :parameters (?a)
    :precondition (and (clear ?a) (onTable ?a) (clawEmpty))
    :effect (and (holding ?a) (not (onTable ?a))
     (not (clawEmpty))))

(:action Drop
    :parameters (?a)
    :precondition (holding ?a)
    :effect (and (clear ?a) (clawEmpty) (onTable ?a)
     (not (holding ?a))))

(:action Put
    :parameters (?a ?b)
    :precondition (and (clear ?b) (holding ?a))
    :effect (and (clawEmpty) (clear ?a) (on ?a ?b)
     (not (clear ?b)) (not (holding ?a))))

(:action Unstack
    :parameters (?a ?b)
    :precondition (and (on ?a ?b) (clear ?a) (clawEmpty))
    :effect (and (holding ?a) (clear ?b)
     (not (on ?a ?b)) (not (clear ?a)) (not (clawEmpty))))

(:action Insert
    :parameters (?a ?b ?c)
    :precondition (and (on ?c ?b) (holding ?a))
    :effect (and (on ?c ?a) (on ?a ?b) (clawEmpty)
     (not (holding ?a)) (not (on ?c ?b)))))
