package com.knz21

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MainTest {

    @Test
    fun testHelpOption() {
        val args = arrayOf("-h")
        val parameters = args.toCommandLineParameters()
        assertEquals(1, parameters.size)
        assertTrue(parameters[0] is CommandLineParameter.OptionArguments)
        val parameter = parameters[0] as CommandLineParameter.OptionArguments
        assertEquals(CommandLineParameter.Option.Help, parameter.option)
        assertEquals(emptyList<String>(), parameter.arguments)
    }

    @Test
    fun testAllOptionWithOneArgument() {
        val args = arrayOf("--apple", "argument")
        val parameters = args.toCommandLineParameters()
        assertEquals(1, parameters.size)
        assertTrue(parameters[0] is CommandLineParameter.OptionArguments)
        val parameter = parameters[0] as CommandLineParameter.OptionArguments
        assertEquals(CommandLineParameter.Option.A, parameter.option)
        assertEquals(listOf("argument"), parameter.arguments)
    }

    @Test
    fun testCatOptionWithTwoArguments() {
        val args = arrayOf("-c", "argument1", "argument2")
        val parameters = args.toCommandLineParameters()
        assertEquals(1, parameters.size)
        assertTrue(parameters[0] is CommandLineParameter.OptionArguments)
        val parameter = parameters[0] as CommandLineParameter.OptionArguments
        assertEquals(CommandLineParameter.Option.C, parameter.option)
        assertEquals(listOf("argument1", "argument2"), parameter.arguments)
    }

    @Test
    fun testMultipleOptions() {
        val args = arrayOf("-h", "--version")
        val parameters = args.toCommandLineParameters()
        assertEquals(2, parameters.size)
        assertTrue(parameters[0] is CommandLineParameter.OptionArguments)
        assertTrue(parameters[1] is CommandLineParameter.OptionArguments)
        val parameter1 = parameters[0] as CommandLineParameter.OptionArguments
        val parameter2 = parameters[1] as CommandLineParameter.OptionArguments
        assertTrue(parameter1.option == CommandLineParameter.Option.Help)
        assertTrue(parameter2.option == CommandLineParameter.Option.Version)
    }

    @Test
    fun testCombinedShortOptions() {
        val args = arrayOf("-abc")
        val parameters = args.toCommandLineParameters()
        assertEquals(3, parameters.size)
        assertTrue(parameters.contains(CommandLineParameter.OptionArguments(CommandLineParameter.Option.A)))
        assertTrue(parameters.contains(CommandLineParameter.OptionArguments(CommandLineParameter.Option.B)))
        assertTrue(parameters.contains(CommandLineParameter.OptionArguments(CommandLineParameter.Option.C)))
    }

    @Test
    fun testShortOptionWithDirectArgument() {
        val args = arrayOf("-aRingo", "-b", "--unknown", "-hv", "--cat=Neko", "Argument")
        val parameters = args.toCommandLineParameters()
        assertEquals(7, parameters.size)
        assertTrue(parameters[0] is CommandLineParameter.OptionArguments)
        assertTrue(parameters[1] is CommandLineParameter.OptionArguments)
        assertTrue(parameters[2] is CommandLineParameter.CommandArgument)
        assertTrue(parameters[3] is CommandLineParameter.OptionArguments)
        assertTrue(parameters[4] is CommandLineParameter.OptionArguments)
        assertTrue(parameters[5] is CommandLineParameter.OptionArguments)
        assertTrue(parameters[6] is CommandLineParameter.CommandArgument)
        val parameter1 = parameters[0] as CommandLineParameter.OptionArguments
        val parameter2 = parameters[1] as CommandLineParameter.OptionArguments
        val parameter3 = parameters[2] as CommandLineParameter.CommandArgument
        val parameter4 = parameters[3] as CommandLineParameter.OptionArguments
        val parameter5 = parameters[4] as CommandLineParameter.OptionArguments
        val parameter6 = parameters[5] as CommandLineParameter.OptionArguments
        val parameter7 = parameters[6] as CommandLineParameter.CommandArgument
        assertEquals(CommandLineParameter.Option.A, parameter1.option)
        assertEquals(listOf("Ringo"), parameter1.arguments)
        assertEquals(CommandLineParameter.Option.B, parameter2.option)
        assertEquals("--unknown", parameter3.value)
        assertEquals(CommandLineParameter.Option.Help, parameter4.option)
        assertEquals(CommandLineParameter.Option.Version, parameter5.option)
        assertEquals(CommandLineParameter.Option.C, parameter6.option)
        assertEquals(listOf("Neko"), parameter6.arguments)
        assertEquals("Argument", parameter7.value)
    }
}
