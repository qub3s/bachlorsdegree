import tested
import numpy as np

def test_second():
    n = np.array(tested.array_return())
    unique, counts = np.unique(n, return_counts=True)
    assert counts.max () == 2

    
