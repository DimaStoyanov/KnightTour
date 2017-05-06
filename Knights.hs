import Data.List
import Debug.Trace

data Answer a = Answer Int [a] deriving Show

instance Eq (Answer a) where
  (Answer a _) == (Answer b _) = a == b

instance Ord (Answer a) where
  (Answer a _) <= (Answer b _) = a <= b


step x y n = step' (x,y) n [] 0
  where
    step' (x,y) n b s =
      let
        nextStep = s + 1
        nb = (nextStep, (x,y)):b
      in
      if x >= 0 &&
         y >= 0 &&
         x < n &&
         y < n &&
         find (\(_, (xx, yy)) -> x == xx && y == yy) b == Nothing
      then
        trace (show b) $
        maximum
        [
          step' (x+2, y+1) n nb nextStep,
          step' (x-2, y+1) n nb nextStep,
          step' (x+2, y-1) n nb nextStep,
          step' (x-2, y-1) n nb nextStep,
          step' (x-1, y-2) n nb nextStep,
          step' (x+1, y-2) n nb nextStep,
          step' (x-1, y+2) n nb nextStep,
          step' (x+1, y+2) n nb nextStep
        ]
      else
         --length b
         Answer (length b) b

main = trace ( show $ (\(Answer _ l) -> l) $ step 0 0 4) (putStrLn "end")
