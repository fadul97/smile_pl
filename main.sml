TheBeginning
    a <=> 10 is int..
    var <=> 10 is int..
    x <=> var + 10 is int..
    _var <=> "Hello, World!\n" is string..

    if var < x then (:
        b <=> 5 is int..
        write("Valor para b: ")
        read(b)
        write({_var})
    :) elif var > x then (:
        if var > a (:
            var <=> 2..
        :)
    :)ifnot(:
        for 10...0 go (:
            var <=> 3..
        :)
    :)


    write("\n")
    write("oi\n")

    write({var})
    write("\nx = ")
    write({x})
    write("\n")
    write({_var})

    for 0...100 go (:
        var <=> 3..
    :)

    for 10...0 go (:
        var <=> 3..
    :)

    while x > 10(:
        write({_var})
        x <=> x - 5..
    :)

    write("\nx = ")
    write({x})
    write("\n")
    write("Valor para var: ")
    read({var})
    write("\n")

    while a (:
        var <=> 3..
    :)
    
TheEnd