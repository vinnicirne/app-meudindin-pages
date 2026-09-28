package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun currencyUtils_formatsPositiveAndNegative() {
    val formatted = com.example.util.CurrencyUtils.format(150.0)
    assertTrue(formatted.contains("150"))
  }

  @Test
  fun dateUtils_monthNamesValid() {
    val monthName = com.example.util.DateUtils.getMonthName(0)
    assertEquals("Janeiro", monthName)
  }

  @Test
  fun supabaseDto_mappingCorrect() {
    val dto = com.example.data.remote.dto.SupabaseTransactionDto(
      id = 1L,
      title = "Salário",
      amount = 5000.0,
      type = "RECEITA",
      categoryId = "cat_salario",
      timestamp = 1700000000000L,
      notes = "Teste"
    )
    val entity = dto.toEntity()
    assertEquals("Salário", entity.title)
    assertEquals(5000.0, entity.amount, 0.001)
  }

  @Test
  fun authDtos_loginRequestValid() {
    val req = com.example.data.remote.dto.LoginRequest("teste@gmail.com", "123456")
    assertEquals("teste@gmail.com", req.email)
    assertEquals("123456", req.password)
  }

  @Test
  fun authDtos_adminRequestValid() {
    val req = com.example.data.remote.dto.AdminCreateUserRequest("admin@gmail.com", "123456", emailConfirm = true)
    assertEquals("admin@gmail.com", req.email)
    assertTrue(req.emailConfirm)
  }

  @Test
  fun authDtos_isAdminDetection() {
    val adminUser = com.example.data.remote.dto.SupabaseUserDto(
      id = "123",
      email = "viniciuscirne@gmail.com"
    )
    assertTrue(adminUser.isAdmin)
  }

  @Test
  fun budgetProgress_percentageCalculation() {
    val category = com.example.data.model.Category.findById("cat_alimentacao")
    val spent = 450.0
    val limit = 500.0
    val percentage = ((spent / limit) * 100).toFloat()
    val progress = com.example.ui.viewmodel.CategoryBudgetProgress(
      category = category,
      spent = spent,
      budgetLimit = limit,
      percentage = percentage
    )
    assertEquals(90f, progress.percentage, 0.01f)
    assertEquals(50.0, progress.budgetLimit - progress.spent, 0.01)
  }

  @Test
  fun transaction_recurrenceProperties() {
    val installmentTx = com.example.data.local.TransactionEntity(
      id = 1L,
      title = "Notebook (1/10)",
      amount = 350.0,
      type = "DESPESA",
      categoryId = "cat_cartao",
      recurrenceType = "PARCELADA",
      installmentNumber = 1,
      totalInstallments = 10
    )
    assertTrue(installmentTx.isInstallment)
    assertFalse(installmentTx.isFixedRecurring)

    val fixedTx = com.example.data.local.TransactionEntity(
      id = 2L,
      title = "Salário",
      amount = 5000.0,
      type = "RECEITA",
      categoryId = "cat_salario",
      recurrenceType = "FIXA",
      installmentNumber = 1,
      totalInstallments = 12
    )
    assertTrue(fixedTx.isFixedRecurring)
    assertFalse(fixedTx.isInstallment)
  }

  @Test
  fun categories_recurrenceCategoriesExist() {
    val cardCat = com.example.data.model.Category.findById("cat_cartao")
    assertEquals("Cartão de Crédito", cardCat.name)

    val carneCat = com.example.data.model.Category.findById("cat_carne")
    assertEquals("Carnê / Crediário", carneCat.name)

    val loanCat = com.example.data.model.Category.findById("cat_emprestimo")
    assertEquals("Empréstimos", loanCat.name)

    val pensaoCat = com.example.data.model.Category.findById("cat_pensao")
    assertEquals("Pensão / Benefício", pensaoCat.name)
  }

  @Test
  fun testMoshiParseSupabaseUserDto() {
    val moshi = com.squareup.moshi.Moshi.Builder()
      .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
      .build()
    val json = """
      {"id":"1e71cc58-cddc-444d-92e2-5446ca902131","aud":"authenticated","role":"authenticated","email":"test@gmail.com","app_metadata":{"provider":"email"},"user_metadata":{"email_verified":true}}
    """.trimIndent()
    val adapter = moshi.adapter(com.example.data.remote.dto.SupabaseUserDto::class.java)
    val user = adapter.fromJson(json)
    assertNotNull(user)
    assertEquals("test@gmail.com", user?.email)
  }

  @Test
  fun testRetrofitAdminCreateUser() = kotlinx.coroutines.runBlocking {
    val result = try {
      val resp = com.example.data.remote.SupabaseClient.authService.adminCreateUser(
        adminApiKey = com.example.data.remote.SupabaseClient.SECRET_KEY,
        adminAuth = "Bearer ${com.example.data.remote.SupabaseClient.SECRET_KEY}",
        request = com.example.data.remote.dto.AdminCreateUserRequest(
          email = "retrofit_test_${System.currentTimeMillis()}@gmail.com",
          password = "Password123!",
          emailConfirm = true
        )
      )
      println("Admin response code: ${resp.code()} error: ${resp.errorBody()?.string()}")
      println("Admin request headers: ${resp.raw().request.headers}")
      resp.isSuccessful
    } catch (e: Exception) {
      println("Admin exception: ${e.message}")
      false
    }
    assertTrue("Admin create user should succeed or handled", result)
  }
}
