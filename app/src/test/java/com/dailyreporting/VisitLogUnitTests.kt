package com.dailyreporting

import android.os.Build
import com.dailyreporting.app.database.VisitLogRepo
import com.dailyreporting.app.models.VisitLog
import com.github.javafaker.Faker
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.Instant
import java.util.*


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class VisitLogUnitTests {

    @Test
    fun testAll() {
        save()
        getAll()
        get()
        delete()
    }


    @Test
    fun save() {
        val data = VisitLog()
        val faker = Faker()

        data.PersonName = faker.name().fullName()
        data.Title = faker.address().cityName()
        data.CompanyName =faker.company().name()
        data.Note = faker.book().author()
        data.VisitReason=faker.address().cityName()
        data.AddedBy = 1
        data.AddedOn = Date.from(Instant.now()).toString();
        data.WORK_LOG_DATE = Date.from(Instant.now()).toString();


        var  res = VisitLogRepo.Save(data)

    }

    @Test
    fun getAll() {
        val res = VisitLogRepo.GetAll();
        assert(res.count() > 0);
    }

    @Test
    fun get() {
        val act = VisitLogRepo.Get(1)
        assert(act.id > 0);
    }

    @Test
    fun delete() {
        val res = VisitLogRepo.Delete(1)
        assert(res);
    }
}