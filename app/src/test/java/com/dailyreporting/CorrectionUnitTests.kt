package com.dailyreporting

import android.os.Build
import com.dailyreporting.app.database.CorrectionsRepo
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
class CorrectionUnitTests {

    @Test
    fun testAll() {
        save()
        getAll()
        get()
        delete()
    }


    @Test
    fun save() {
     /*   val data = Correction()
        val faker = Faker()
        data.AddedBy = 1
        data.AddedOn = Date.from(Instant.now());
        data.CodeId = 1;
        data.Description = faker.name().fullName()
        CorrectionsRepo.Save(data)
        assert(data.id > 0);*/
    }

    @Test
    fun getAll() {
        val res = CorrectionsRepo.GetAll();
        assert(res.count() > 0);
    }

    @Test
    fun get() {
        val act = CorrectionsRepo.Get(1)
        assert(act.id > 0);
    }

    @Test
    fun delete() {
        val res = CorrectionsRepo.Delete(1)
        assert(res);
    }
}