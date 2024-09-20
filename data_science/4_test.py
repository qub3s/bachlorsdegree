import tested
import numpy as np

def test_fourth():
    n = np.array(tested.array_return())
    unique, counts = np.unique(n, return_counts=True)

    assert not (counts==1).sum() < 1

    
