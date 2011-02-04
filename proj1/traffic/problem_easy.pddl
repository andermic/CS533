(define (problem trafficProblem)
(:domain trafficJam)
(:objects
   sq_0_0
   sq_0_1
   sq_0_2
   sq_0_3
   sq_0_4
   sq_0_5
   sq_1_0
   sq_1_1
   sq_1_2
   sq_1_3
   sq_1_4
   sq_1_5
   sq_2_0
   sq_2_1
   sq_2_2
   sq_2_3
   sq_2_4
   sq_2_5
   sq_3_0
   sq_3_1
   sq_3_2
   sq_3_3
   sq_3_4
   sq_3_5
   sq_4_0
   sq_4_1
   sq_4_2
   sq_4_3
   sq_4_4
   sq_4_5
   sq_5_0
   sq_5_1
   sq_5_2
   sq_5_3
   sq_5_4
   sq_5_5

   v0
   v1
   v6

   v5

   v7

   v2
   v3
   v4
)

(:init
   (SQUARE sq_0_0)
   (SQUARE sq_0_1)
   (SQUARE sq_0_2)
   (SQUARE sq_0_3)
   (SQUARE sq_0_4)
   (SQUARE sq_0_5)
   (SQUARE sq_1_0)
   (SQUARE sq_1_1)
   (SQUARE sq_1_2)
   (SQUARE sq_1_3)
   (SQUARE sq_1_4)
   (SQUARE sq_1_5)
   (SQUARE sq_2_0)
   (SQUARE sq_2_1)
   (SQUARE sq_2_2)
   (SQUARE sq_2_3)
   (SQUARE sq_2_4)
   (SQUARE sq_2_5)
   (SQUARE sq_3_0)
   (SQUARE sq_3_1)
   (SQUARE sq_3_2)
   (SQUARE sq_3_3)
   (SQUARE sq_3_4)
   (SQUARE sq_3_5)
   (SQUARE sq_4_0)
   (SQUARE sq_4_1)
   (SQUARE sq_4_2)
   (SQUARE sq_4_3)
   (SQUARE sq_4_4)
   (SQUARE sq_4_5)
   (SQUARE sq_5_0)
   (SQUARE sq_5_1)
   (SQUARE sq_5_2)
   (SQUARE sq_5_3)
   (SQUARE sq_5_4)
   (SQUARE sq_5_5)

   (VEHICLE_2H v0)
   (VEHICLE_2H v1)
   (VEHICLE_2H v6)

   (VEHICLE_2V v5)

   (VEHICLE_3H v7)

   (VEHICLE_3V v2)
   (VEHICLE_3V v3)
   (VEHICLE_3V v4)

   (next_to_right sq_0_0 sq_0_1)
   (next_to_right sq_0_1 sq_0_2)
   (next_to_right sq_0_2 sq_0_3)
   (next_to_right sq_0_3 sq_0_4)
   (next_to_right sq_0_4 sq_0_5)
   (next_to_right sq_1_0 sq_1_1)
   (next_to_right sq_1_1 sq_1_2)
   (next_to_right sq_1_2 sq_1_3)
   (next_to_right sq_1_3 sq_1_4)
   (next_to_right sq_1_4 sq_1_5)
   (next_to_right sq_2_0 sq_2_1)
   (next_to_right sq_2_1 sq_2_2)
   (next_to_right sq_2_2 sq_2_3)
   (next_to_right sq_2_3 sq_2_4)
   (next_to_right sq_2_4 sq_2_5)
   (next_to_right sq_3_0 sq_3_1)
   (next_to_right sq_3_1 sq_3_2)
   (next_to_right sq_3_2 sq_3_3)
   (next_to_right sq_3_3 sq_3_4)
   (next_to_right sq_3_4 sq_3_5)
   (next_to_right sq_4_0 sq_4_1)
   (next_to_right sq_4_1 sq_4_2)
   (next_to_right sq_4_2 sq_4_3)
   (next_to_right sq_4_3 sq_4_4)
   (next_to_right sq_4_4 sq_4_5)
   (next_to_right sq_5_0 sq_5_1)
   (next_to_right sq_5_1 sq_5_2)
   (next_to_right sq_5_2 sq_5_3)
   (next_to_right sq_5_3 sq_5_4)
   (next_to_right sq_5_4 sq_5_5)

   (next_to_up sq_1_0 sq_0_0)
   (next_to_up sq_1_1 sq_0_1)
   (next_to_up sq_1_2 sq_0_2)
   (next_to_up sq_1_3 sq_0_3)
   (next_to_up sq_1_4 sq_0_4)
   (next_to_up sq_1_5 sq_0_5)
   (next_to_up sq_2_0 sq_1_0)
   (next_to_up sq_2_1 sq_1_1)
   (next_to_up sq_2_2 sq_1_2)
   (next_to_up sq_2_3 sq_1_3)
   (next_to_up sq_2_4 sq_1_4)
   (next_to_up sq_2_5 sq_1_5)
   (next_to_up sq_3_0 sq_2_0)
   (next_to_up sq_3_1 sq_2_1)
   (next_to_up sq_3_2 sq_2_2)
   (next_to_up sq_3_3 sq_2_3)
   (next_to_up sq_3_4 sq_2_4)
   (next_to_up sq_3_5 sq_2_5)
   (next_to_up sq_4_0 sq_3_0)
   (next_to_up sq_4_1 sq_3_1)
   (next_to_up sq_4_2 sq_3_2)
   (next_to_up sq_4_3 sq_3_3)
   (next_to_up sq_4_4 sq_3_4)
   (next_to_up sq_4_5 sq_3_5)
   (next_to_up sq_5_0 sq_4_0)
   (next_to_up sq_5_1 sq_4_1)
   (next_to_up sq_5_2 sq_4_2)
   (next_to_up sq_5_3 sq_4_3)
   (next_to_up sq_5_4 sq_4_4)
   (next_to_up sq_5_5 sq_4_5)

   (at_2 v0 sq_2_1 sq_2_2)
   (at_2 v1 sq_0_0 sq_0_1)
   (at_2 v5 sq_4_0 sq_5_0)
   (at_2 v6 sq_4_4 sq_4_5)
   (at_3 v2 sq_0_5 sq_1_5 sq_2_5)
   (at_3 v3 sq_1_0 sq_2_0 sq_3_0)
   (at_3 v4 sq_1_3 sq_2_3 sq_3_3)
   (at_3 v7 sq_5_2 sq_5_3 sq_5_4)

   (empty sq_0_2)
   (empty sq_0_3)
   (empty sq_0_4)
   (empty sq_1_1)
   (empty sq_1_2)
   (empty sq_1_4)
   (empty sq_2_4)
   (empty sq_3_1)
   (empty sq_3_2)
   (empty sq_3_4)
   (empty sq_3_5)
   (empty sq_4_1)
   (empty sq_4_2)
   (empty sq_4_3)
   (empty sq_5_1)
   (empty sq_5_5)
)

(:goal (and
   (at_2 v0 sq_2_4 sq_2_5)
))
)
