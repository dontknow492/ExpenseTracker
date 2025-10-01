package org.ghost.expensetracker.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.ghost.expensetracker.core.utils.sha256
import org.ghost.expensetracker.data.database.dao.CategoryDao
import org.ghost.expensetracker.data.database.dao.ProfileDao
import org.ghost.expensetracker.data.database.entity.ProfileEntity
import org.ghost.expensetracker.data.mappers.toDomainModel
import org.ghost.expensetracker.data.mappers.toEntity
import org.ghost.expensetracker.data.models.Category
import org.ghost.expensetracker.data.models.CategoryWithExpenseCount
import org.ghost.expensetracker.data.models.Profile
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val profileDao: ProfileDao,
    private val categoryDao: CategoryDao
) {
    suspend fun createProfile(profile: Profile, email: String, password: String): Long {
        return profileDao.insert(profile.toEntity(email, password.sha256()))
    }

    fun getProfileById(id: Long): Flow<Profile?> {
        val profileEntityFlow: Flow<ProfileEntity?> = profileDao.getProfileById(id)
        return profileEntityFlow.map { profileEntity ->
            profileEntity?.toDomainModel()
        }
    }

    suspend fun checkProfileExists(email: String, password: String): Boolean {
        val passwordHash = password.sha256()
        val profile = getProfileByEmailAndPasswordHash(email, passwordHash).firstOrNull()
        return profile != null
    }

    suspend fun checkProfileExists(email: String, firstName: String, lastName: String): Boolean {
        val profile = getProfileByEmailAndName(
            email = email,
            firstName = firstName,
            lastName = lastName
        ).firstOrNull()
        return profile != null
    }


    fun getProfileByEmailAndPasswordHash(email: String, passwordHash: String): Flow<Profile?> {
        val profileEntityFlow: Flow<ProfileEntity?> =
            profileDao.getProfileByEmailAndPasswordHash(email, passwordHash)
        return profileEntityFlow.map { profileEntity ->
            profileEntity?.toDomainModel()
        }
    }

    fun getProfileByEmailAndName(
        email: String,
        firstName: String,
        lastName: String
    ): Flow<Profile?> {
        val profileEntityFlow: Flow<ProfileEntity?> =
            profileDao.getProfileByEmailAndName(email, firstName, lastName)
        return profileEntityFlow.map { profileEntity ->
            profileEntity?.toDomainModel()
        }
    }

    suspend fun updateProfile(profile: Profile, email: String, password: String): Boolean {
        return profileDao.update(
            profile.toEntity(
                email,
                password.sha256()
            )
        ) > 0
    }

    suspend fun updateProfile(profile: Profile): Boolean {
        val profileEntity = profileDao.getProfileById(profile.id).firstOrNull() ?: return false
        return profileDao.update(profileEntity) > 0
    }

    suspend fun deleteProfileByCredentials(
        profileId: Long,
        email: String,
        password: String
    ): Boolean {
        // 1. Hash the password
        val passwordHash = password.sha256()

        // 2. Call the single, atomic DAO function
        val rowsDeleted = profileDao.deleteProfileWithCredentials(
            id = profileId,
            email = email,
            passwordHash = passwordHash
        )

        // 3. Return true only if exactly one row was deleted.
        return rowsDeleted > 0
    }


    suspend fun createCategory(category: Category): Long {
        return categoryDao.insert(category.toEntity())
    }

    suspend fun updateCategory(category: Category) {
        categoryDao.update(category.toEntity())
    }

    suspend fun updateCategories(categories: List<Category>) {
        categoryDao.updateCategories(categories.map { it.toEntity() })
    }

    suspend fun deleteCategoryById(categoryId: Long): Boolean {
        return categoryDao.deleteCategoryById(categoryId) > 0
    }

    fun getCategoryById(categoryId: Long): Flow<Category?> {
        return categoryDao.getCategoryById(categoryId).map {
            it?.toDomainModel()
        }
    }

    fun getCategoryByNameAndProfile(name: String, profileOwnerId: Long): Flow<Category?> {
        return categoryDao.getCategoryByNameAndProfile(name, profileOwnerId).map {
            it?.toDomainModel()
        }
    }

    suspend fun getCategoryCountForProfile(profileOwnerId: Long): Int {
        return categoryDao.getCategoryCountForProfile(profileOwnerId)
    }

    fun getAllCategoriesForProfile(profileOwnerId: Long): Flow<List<Category>> {
        return categoryDao
            .getAllCategoriesForProfile(profileOwnerId)
            .map { categoryEntities ->
                categoryEntities.map { categoryEntity ->
                    categoryEntity.toDomainModel()
                }
            }
    }

    fun getCategoriesWithExpenseCount(
        profileOwnerId: Long,
        minDate: Long?,
        maxDate: Long?
    ): Flow<List<CategoryWithExpenseCount>> {
        return categoryDao.getCategoriesWithExpenseCount(
            profileOwnerId = profileOwnerId,
            minDate = minDate,
            maxDate = maxDate
        ).map { entities ->
            entities.map {
                it.toDomainModel()
            }
        }
    }


}
