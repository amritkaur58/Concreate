package com.dailyreporting

import android.os.Build
import com.dailyreporting.app.database.WorkLogsRepo
import com.dailyreporting.app.models.WorkLog
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
class WorkLogUnitTests {

    @Test
    fun testAll() {
        save()
        getAll()
        get()
        delete()
    }


    @Test
    fun save() {
        val data = WorkLog()
        val faker = Faker()
        data.Title = faker.name().fullName()
        data.AddedBy = 1
        data.AddedOn = Date.from(Instant.now());
        WorkLogsRepo.Save(data)
        assert(data.id > 0);
    }

    @Test
    fun getAll() {
        val res = WorkLogsRepo.GetAll();
        assert(res.count() > 0);
    }

    @Test
    fun get() {
        val act = WorkLogsRepo.Get(1)
        assert(act.id > 0);
    }

    @Test
    fun delete() {
        val res = WorkLogsRepo.Delete(1)
        assert(res);
    }
}