package com.dailyreporting

import com.dailyreporting.app.database.DailyFolderRepo
import com.dailyreporting.app.models.DailyFolder
import com.github.javafaker.Faker
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ValidatorTest {

    @Test
    fun testAll() {
        save()

    }

    @Test
    fun save() {
        val act = DailyFolder()
        val faker = Faker()
        act.note = faker.address().city()
        act.image = faker.name().fullName()
        act.AddedOn = faker.date().toString()

        DailyFolderRepo.Save(act)
        assert(act.id > 0)
    }


}