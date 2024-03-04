package com.knz21

fun main(args: Array<String>) {
    println(args.joinToString())
    val parameters: List<CommandLineParameter> = args.toCommandLineParameters()
    parameters.forEach(::println)
}
