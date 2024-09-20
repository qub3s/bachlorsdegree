import tested

def test_first():
    l = len(tested.array_return())%2
    assert l == 1
