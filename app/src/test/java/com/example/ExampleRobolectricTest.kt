package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.VantaRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Vanta Digital", appName)
    }

    @Test
    fun `test admin credentials login and profile update`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = VantaRepository.getRepository(context)

        // Login with specified admin email and password
        val loginResult = repository.login("sehar.asma30@gmail.com", "Muhammad@0101")
        assertTrue("Admin login should succeed", loginResult.isSuccess)
        val admin = loginResult.getOrNull()
        assertNotNull(admin)
        assertEquals("ADMIN", admin?.role)
        assertEquals("sehar.asma30@gmail.com", admin?.email)

        // Test updating admin profile credentials
        val updatedAdmin = admin!!.copy(
            name = "Sehar Asma Director",
            businessName = "Vanta Digital International"
        )
        val updateResult = repository.updateUserProfile(updatedAdmin)
        assertTrue("Profile update should succeed", updateResult.isSuccess)
        assertEquals("Sehar Asma Director", repository.currentUser.value?.name)
    }

    @Test
    fun `test dummy packages csv template generation and import`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = VantaRepository.getRepository(context)

        val csvTemplate = repository.generateDummyCsvTemplate()
        assertTrue("CSV template should contain Tier_ID", csvTemplate.contains("Tier_ID"))
        assertTrue("CSV template should contain 20 tiers", csvTemplate.contains("tier_20"))

        val importResult = repository.importPackagesFromCsv(csvTemplate)
        assertTrue("Import should succeed", importResult.isSuccess)
        assertEquals(20, importResult.getOrNull())
    }
}
