package com.dailyreporting

import android.os.Build
import com.dailyreporting.app.database.ActivitiesRepo
import com.dailyreporting.app.models.ActivityModel
import com.github.javafaker.Faker
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class ActivityUnitTests {

    @Test
    fun testAll() {
        save()
        getAll()
        get()
        delete()
    }


    @Test
    fun save() {
        val act = ActivityModel()
        val faker = Faker()
        act.activityname = faker.name().fullName()
        act.code =  faker.name().fullName()
        act.complaint = "1";
        act.imagepath =  faker.name().fullName()
        act.location =  faker.name().fullName()
        act.note =  faker.name().fullName()
        act.taskcompleted = "1"
        act.userid = 1
        ActivitiesRepo.Save(act)
        assert(act.id > 0);
    }

    @Test
    fun getAll() {
        val res = ActivitiesRepo.GetAll();
        assert(res.count() > 0);
    }

    @Test
    fun get() {
        val act = ActivitiesRepo.Get(1)
        assert(act.id > 0);
    }

    @Test
    fun delete() {
        val res = ActivitiesRepo.Delete(1)
        assert(res);
    }
}