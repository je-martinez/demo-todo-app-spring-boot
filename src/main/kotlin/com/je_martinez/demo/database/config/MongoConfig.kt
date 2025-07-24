package com.je_martinez.demo.database.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration

@Configuration
class MongoConfig(
    @Autowired private val env: Environment
): AbstractMongoClientConfiguration() {

    companion object{
        fun  getDatabaseName(uri:String):String? {
            try{
                val connectionString = com.mongodb.ConnectionString(uri)
                print(connectionString.database)
                return connectionString.database
            }catch (_: Exception){
                return null;
            }
        }
    }

    override fun getDatabaseName(): String {
        return getDatabaseName(env.getProperty("spring.data.mongodb.uri") ?: "") ?: throw Exception("It required a valid database connection")
    }

    override fun autoIndexCreation(): Boolean {
        return true
    }
}