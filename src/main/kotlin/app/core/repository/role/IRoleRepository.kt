package app.core.repository.role

interface IRoleRepository {

    /**
     * Adds a new role to the database.
     *
     * @param roleName the name of the role to check
     * @param requiresApproval boolean value whether role needs approval or not
     * @return 1 if the role added, 0 otherwise
     */
    fun addNewRole(roleName: String, requiresApproval: Boolean): Int

    /**
     * Deletes a role from the database.
     *
     * @param roleName the name of the role to delete
     * @return 1 if the role deleted, 0 otherwise
     */
    fun deleteRole(roleName: String): Int

    /**
     * Updates whether a role needs to be approved or not.
     *
     * @param roleName the name of the role
     * @param requiresApproval boolean value whether role needs approval or not
     * @return 1 if the role updated, 0 otherwise
     */
    fun updateRoleApproval(roleName: String, requiresApproval: Boolean): Int

    /**
     * Checks whether a role requires approval before it can be assigned.
     *
     * @param roleName the name of the role to check
     * @return true if the role requires approval, false otherwise
     */
    fun requiresApproval(roleName: String): Boolean

    /**
     * Retrieves a list of all roles available in the system.
     *
     * @return list of role names
     */
    fun getAllRoles(): List<String>
}
