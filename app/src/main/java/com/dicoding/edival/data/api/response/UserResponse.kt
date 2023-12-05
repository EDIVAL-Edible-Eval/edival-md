package com.dicoding.edival.data.api.response

import com.google.gson.annotations.SerializedName

data class UserResponse(

	@field:SerializedName("reminders")
	val reminders: List<RemindersItem?>? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("theme_preference")
	val themePreference: String? = null,

	@field:SerializedName("profile_img_path")
	val profileImgPath: String? = null,

	@field:SerializedName("language_preference")
	val languagePreference: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)

data class RemindersItem(

	@field:SerializedName("storage_type")
	val storageType: String? = null,

	@field:SerializedName("store_date")
	val storeDate: String? = null,

	@field:SerializedName("img_path")
	val imgPath: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("exp_date")
	val expDate: String? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
