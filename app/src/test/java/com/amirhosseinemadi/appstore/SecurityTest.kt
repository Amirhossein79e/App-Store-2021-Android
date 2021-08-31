package com.amirhosseinemadi.appstore

import com.amirhosseinemadi.appstore.common.SecurityManager
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SecurityTest {


    //For this test need to change Android Base64 with Java Base64 in method
    @Test
    fun testEnc()
    {
        val data:String = SecurityManager.encrypt("hello").get("output") as String
        Assert.assertNotNull(data)
        MatcherAssert.assertThat(data.length,Matchers.greaterThan(20))
        print(data)
    }


}