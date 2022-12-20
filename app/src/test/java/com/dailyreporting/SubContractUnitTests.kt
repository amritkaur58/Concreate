package com.dailyreporting

import android.os.Build
import com.dailyreporting.app.database.ActivitiesRepo
import com.dailyreporting.app.database.SubContractorRepo
import com.dailyreporting.app.models.ActivityModel
import com.dailyreporting.app.models.SubContractActivity
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
class SubContractUnitTests {

    @Test
    fun testAll() {
        save()
        getAll()
        get()
        delete()
    }


    @Test
    fun save() {
        val act = SubContractActivity()
        val faker = Faker()
        act.activityname = faker.name().fullName()
        act.contractorName = faker.name().fullName()
        act.code =  faker.name().fullName()
        act.imagepath =  faker.name().fullName()
        act.location =  faker.name().fullName()

        SubContractorRepo.Save(act)
        assert(act.id > 0);
    }

    @Test
    fun getAll() {
        val res = SubContractorRepo.GetAll();
        assert(res.count() > 0);
    }

    @Test
    fun get() {
        val act = SubContractorRepo.Get(1)
        assert(act.id > 0);
    }

    @Test
    fun delete() {
        val res = SubContractorRepo.Delete(1)
        assert(res);
    }
}