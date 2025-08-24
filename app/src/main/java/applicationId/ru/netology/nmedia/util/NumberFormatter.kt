package ru.netology.nmedia.util

object NumberFormatter {
    fun formatCount(count: Int): String {
        return when {
            count < 1000 -> count.toString()
            count < 10_000 -> {
                val thousands = count / 1000
                val hundreds = (count % 1000) / 100
                if (hundreds == 0) "${thousands}K" else "${thousands}.${hundreds}K"
            }
            count < 1_000_000 -> "${count / 1000}K"
            else -> {
                val millions = count / 1_000_000
                val hundredThousands = (count % 1_000_000) / 100_000
                if (hundredThousands == 0) "${millions}M" else "${millions}.${hundredThousands}M"
            }
        }
    }
}