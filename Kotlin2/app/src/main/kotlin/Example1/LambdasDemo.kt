package Example1

/**
 * Created by Y on 9/16/2017.
 */


fun manipulateInt(value: Int, transform: (Int) -> Int) :  Int {
    val myNewInt = transform(value)
    return myNewInt
}


fun testLambda1() {
    val a = 5
    val b = manipulateInt(a, { x -> x*2 })
    println(b)
}



















fun <T> filter(array: List<T>, condition: (T) -> Boolean): List<T> {
    var filteredArray = arrayListOf<T>()

    //try the suggestion by the compiler below
    for (obj in array) {
        if(condition(obj)) {
            filteredArray.add(obj)
        }
    }

    return filteredArray

}

data class Person(var name: String, var age: Int)

fun testLambda2() {

    val people = listOf( Person("John", 27), Person("Joseph", 12), Person("Jane", 17) )

    var underaged = filter(people, {p -> p.age < 18})

    underaged.forEach ({ p -> println(p.name)})

}

