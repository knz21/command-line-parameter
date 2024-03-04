package com.knz21

private const val SHORT_OPTION_PREFIX = "-"

private const val LONG_OPTION_PREFIX = "--"

private const val OPTION_ARGUMENT_DELIMITER = '='

sealed interface CommandLineParameter {

    data class CommandArgument(val value: String) : CommandLineParameter

    data class OptionArguments(val option: Option, val arguments: List<String> = emptyList()) : CommandLineParameter

    enum class Option(val short: Char? = null, val long: String? = null, val availableArgumentSize: Int = 0) {
        A('a', "apple", 1),
        B('b', "blue"),
        C('c', "cat", 2),
        Help('h', "help"),
        Version('v', "version");

        companion object {

            fun findByShortName(value: Char?): Option? = entries.find { it.short == value }

            fun findByLongName(value: String?): Option? = entries.find { it.long == value }
        }
    }
}

fun Array<String>.toCommandLineParameters(): List<CommandLineParameter> {
    val parameters = mutableListOf<CommandLineParameter>()
    val processedIndices = mutableSetOf<Int>()
    for (i in indices) {
        if (i in processedIndices) continue
        val argument = this[i]
        if (argument.startsWith(LONG_OPTION_PREFIX)) {
            if (argument.contains(OPTION_ARGUMENT_DELIMITER)) {
                // --option=Argument
                val option = CommandLineParameter.Option.findByLongName(
                    argument.drop(2).substringBefore(OPTION_ARGUMENT_DELIMITER)
                )
                if (option != null && option.availableArgumentSize > 0) {
                    val optionArguments = argument.substringAfter(OPTION_ARGUMENT_DELIMITER)
                    parameters.add(CommandLineParameter.OptionArguments(option, listOf(optionArguments)))
                    continue
                }
            } else if (argument.length > 2) {
                // --option
                val option = CommandLineParameter.Option.findByLongName(argument.drop(2))
                if (option != null) {
                    val nextIndex = i + 1
                    val optionArguments = mutableListOf<String>()
                    for (j in nextIndex until nextIndex + option.availableArgumentSize) {
                        val optionArgument = getOrNull(j)
                        if (optionArgument != null && !optionArgument.isOption()) {
                            processedIndices.add(j)
                            optionArguments.add(optionArgument)
                        } else {
                            break
                        }
                    }
                    parameters.add(CommandLineParameter.OptionArguments(option, optionArguments))
                    continue
                }
            }
        } else if (argument.startsWith(SHORT_OPTION_PREFIX)) {
            if (argument.isShortSuccessiveOptions()) {
                // -abc
                argument.drop(1).toList().forEach { char ->
                    val option = CommandLineParameter.Option.findByShortName(char)
                    if (option != null) {
                        parameters.add(CommandLineParameter.OptionArguments(option))
                    }
                }
                continue
            }
            val option = CommandLineParameter.Option.findByShortName(argument.getOrNull(1))
            if (option != null) {
                // -a
                when (argument.getOrNull(2)) {
                    // -a=Argument
                    OPTION_ARGUMENT_DELIMITER -> {
                        if (option.availableArgumentSize > 0) {
                            parameters.add(CommandLineParameter.OptionArguments(option, listOf(argument.drop(3))))
                            continue
                        }
                    }
                    // -a
                    null -> {
                        val nextIndex = i + 1
                        val optionArguments = mutableListOf<String>()
                        for (j in nextIndex until nextIndex + option.availableArgumentSize) {
                            val optionArgument = getOrNull(j)
                            if (optionArgument != null && !optionArgument.isOption()) {
                                processedIndices.add(j)
                                optionArguments.add(optionArgument)
                            } else {
                                break
                            }
                        }
                        parameters.add(CommandLineParameter.OptionArguments(option, optionArguments))
                        continue
                    }
                    // -aArgument
                    else -> {
                        if (option.availableArgumentSize > 0) {
                            parameters.add(CommandLineParameter.OptionArguments(option, listOf(argument.drop(2))))
                            continue
                        }
                    }
                }
            }
        }
        parameters.add(CommandLineParameter.CommandArgument(argument))
    }
    return parameters
}

private fun String.isOption(): Boolean = when {
    startsWith(LONG_OPTION_PREFIX) -> {
        val option = CommandLineParameter.Option.findByLongName(drop(2).substringBefore(OPTION_ARGUMENT_DELIMITER))
        if (option != null && option.availableArgumentSize > 0) {
            true
        } else {
            CommandLineParameter.Option.findByLongName(drop(2)) != null
        }
    }
    startsWith(SHORT_OPTION_PREFIX) -> {
        if (isShortSuccessiveOptions()) {
            true
        } else {
            val option = CommandLineParameter.Option.findByShortName(getOrNull(1))
            if (option != null) {
                when (getOrNull(2)) {
                    OPTION_ARGUMENT_DELIMITER -> option.availableArgumentSize > 0
                    null -> true
                    else -> option.availableArgumentSize > 0
                }
            } else {
                false
            }
        }
    }
    else -> false
}

private fun String.isShortSuccessiveOptions(): Boolean = startsWith(SHORT_OPTION_PREFIX) &&
    length > 2 &&
    drop(1).all { char -> CommandLineParameter.Option.findByShortName(char) != null } &&
    toSet().size == length