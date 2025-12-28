// ================= INITIALIZATION & API CONFIG =================

const API_BASE = 'http://localhost:8080/api/';

// Initialize data if empty
function initializeData() {
    // No presave data
}

// Data Helpers - Now using API
async function getUsers() {
    try {
        const response = await fetch(`${API_BASE}users`, { credentials: 'include' });
        const data = await response.json();
        return data.users || [];
    } catch (error) {
        console.error('Error fetching users:', error);
        return [];
    }
}

async function getItems() {
    try {
        const response = await fetch(`${API_BASE}items?_=${Date.now()}`, {
            cache: 'no-cache',
            credentials: 'include'
        });
        const data = await response.json();
        return data.items || [];
    } catch (error) {
        console.error('Error fetching items:', error);
        return [];
    }
}

async function getPendingAdmins() {
    try {
        const response = await fetch(`${API_BASE}users/pending-admins`, { credentials: 'include' });
        const data = await response.json();
        return data.pending_admins || [];
    } catch (error) {
        console.error('Error fetching pending admins:', error);
        return [];
    }
}

async function getCurrentUser() {
    try {
        const response = await fetch(`${API_BASE}users/current`, { credentials: 'include' });
        const data = await response.json();
        return data.user || null;
    } catch (error) {
        console.error('Error fetching current user:', error);
        return null;
    }
}

let currentUser = null;

// ================= NAVIGATION & VIEW HANDLING =================

function switchView(viewId) {
    // Hide all main sections
    document.querySelectorAll('section').forEach(el => el.classList.add('hidden'));
    document.getElementById('main-nav').classList.add('hidden');
    
    // Show target view
    const targetView = document.getElementById(viewId);
    targetView.classList.remove('hidden');

    // Show Nav if it's a dashboard
    if(viewId.includes('dash')) {
         document.getElementById('main-nav').classList.remove('hidden');
    }
}

// Check login status on load
window.onload = async function() {
    initializeData();
    currentUser = await getCurrentUser();
    if (currentUser) {
        document.getElementById('welcome-user-msg').textContent = `Welcome, ${currentUser.fname} (${currentUser.type})`;
        if (currentUser.type === 'Admin') {
            await loadAdminDashboard();
            switchView('admin-dash-view');
        } else {
            await loadUserDashboard();
            switchView('user-dash-view');
        }
    } else {
        switchView('login-view');
    }
};

async function logout() {
    try {
        const response = await fetch(`${API_BASE}users/logout`, {
            method: 'POST',
            credentials: 'include'
        });
        const data = await response.json();

        if (data.message) {
            currentUser = null;
            switchView('login-view');
        } else if (data.error) {
            alert(data.error);
        }
    } catch (error) {
        console.error('Logout error:', error);
        alert('An error occurred during logout. Please try again.');
    }
}




// ================= AUTHENTICATION LOGIC =================

// Show/hide admin request checkbox based on user type selection
document.getElementById('reg-usertype').addEventListener('change', function() {
    console.log('User type changed to:', this.value);
    const adminRequestContainer = document.getElementById('admin-request-container');
    if (this.value === 'Admin') {
        console.log('Showing admin request container');
        adminRequestContainer.style.display = 'flex';
    } else {
        console.log('Hiding admin request container');
        adminRequestContainer.style.display = 'none';
        document.getElementById('reg-request-admin').checked = false;
    }
});

// Signup
document.getElementById('signup-form').addEventListener('submit', async function(e) {
    e.preventDefault();

    const newUser = {
        fname: document.getElementById('reg-fname').value,
        mname: document.getElementById('reg-mname').value,
        lname: document.getElementById('reg-lname').value,
        type: document.getElementById('reg-usertype').value,
        email: document.getElementById('reg-email').value,
        username: document.getElementById('reg-username').value,
        password: document.getElementById('reg-password').value
    };

    // Include admin request if user type is Admin and checkbox is checked
    if (newUser.type === 'Admin') {
        newUser.request_admin = document.getElementById('reg-request-admin').checked;
    }

    try {
        const response = await fetch(`${API_BASE}users`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(newUser)
        });
        const data = await response.json();

        if (data.message) {
            alert(data.message);
            this.reset();
            switchView('login-view');
        } else if (data.error) {
            alert(data.error);
        }
    } catch (error) {
        console.error('Signup error:', error);
        alert('An error occurred during signup. Please try again.');
    }
});

// Login
document.getElementById('login-form').addEventListener('submit', async function(e) {
    e.preventDefault();

    const loginData = {
        username: document.getElementById('login-username').value,
        password: document.getElementById('login-password').value
    };

    try {
        const response = await fetch(`${API_BASE}users/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify(loginData)
        });
        const data = await response.json();

        if (data.user) {
            currentUser = data.user;
            document.getElementById('welcome-user-msg').textContent = `Welcome, ${currentUser.fname} (${currentUser.type})`;
            this.reset();

            if (currentUser.type === 'Admin') {
                await loadAdminDashboard();
                switchView('admin-dash-view');
            } else {
                await loadUserDashboard();
                switchView('user-dash-view');
            }
        } else if (data.error) {
            alert(data.error);
        }
    } catch (error) {
        console.error('Login error:', error);
        alert('An error occurred during login. Please try again.');
    }
});


// ================= ADMIN DASHBOARD LOGIC =================

async function loadAdminDashboard() {
    const users = await getUsers();
    const items = await getItems();
    const pendingAdmins = await getPendingAdmins();
    renderUserTable(users);
    renderAdminItemTable(items);
    renderPendingAdminsTable(pendingAdmins);
}

function renderUserTable(usersList) {
    const tbody = document.getElementById('users-table-body');
    tbody.innerHTML = '';
    usersList.forEach((user, index) => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${user.fname} ${user.mname} ${user.lname}</td>
            <td>${user.username}</td>
            <td>${user.type}</td>
            <td><button class="btn-delete" onclick="removeUser('${user.username}')">Remove</button></td>
        `;
        tbody.appendChild(tr);
    });
}

async function removeUser(username) {
    if(confirm('Are you sure you want to remove this user?')) {
        try {
            const response = await fetch(`${API_BASE}users?username=${username}`, {
                method: 'DELETE',
                credentials: 'include'
            });
            const data = await response.json();

            if (data.message) {
                alert(data.message);
                await loadAdminDashboard(); // Refresh the dashboard
            } else if (data.error) {
                alert(data.error);
            }
        } catch (error) {
            console.error('Error deleting user:', error);
            alert('An error occurred while deleting the user.');
        }
    }
}

function renderAdminItemTable(itemsList) {
    const tbody = document.getElementById('items-table-body');
    tbody.innerHTML = '';
    itemsList.forEach(item => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><img src="${item.image}" class="item-thumb"></td>
            <td>${item.name}<br><small>${item.desc.substring(0,30)}...</small></td>
             <td><span class="status-badge status-${item.status}">${item.status}</span></td>
            <td>${item.reportedBy}</td>
            <td><button class="btn-delete" onclick="removeItem(${item.id})">Remove</button></td>
        `;
        tbody.appendChild(tr);
    });
}

async function removeItem(id) {
    if(confirm('Are you sure you want to delete this item report?')) {
        try {
            const response = await fetch(`${API_BASE}items?id=${id}`, {
                method: 'DELETE',
                credentials: 'include'
            });
            const data = await response.json();

            if (data.message) {
                alert(data.message);
                await loadAdminDashboard(); // Refresh the dashboard
            } else if (data.error) {
                alert(data.error);
            }
        } catch (error) {
            console.error('Error deleting item:', error);
            alert('An error occurred while deleting the item.');
        }
    }
}

async function adminSearchItems() {
    const query = document.getElementById('admin-search').value.toLowerCase();
    const allItems = await getItems();
    const filtered = allItems.filter(item =>
        item.name.toLowerCase().includes(query) ||
        item.desc.toLowerCase().includes(query) ||
        item.status.toLowerCase().includes(query)
    );
    renderAdminItemTable(filtered);
}

function renderPendingAdminsTable(pendingAdminsList) {
    const tbody = document.getElementById('pending-admins-table-body');
    tbody.innerHTML = '';
    if (pendingAdminsList.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4">No pending admin requests.</td></tr>';
        return;
    }
    pendingAdminsList.forEach(admin => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${admin.fname} ${admin.mname} ${admin.lname}</td>
            <td>${admin.username}</td>
            <td>${admin.email}</td>
            <td>
                <button class="btn-approve" onclick="approveAdmin('${admin.username}')">Approve</button>
                <button class="btn-delete" onclick="removeUser('${admin.username}')">Reject</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

async function approveAdmin(username) {
    if (confirm('Are you sure you want to approve this admin request?')) {
        try {
            const response = await fetch(`${API_BASE}users/approve-admin`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify({
                    username: username,
                    approver_username: currentUser.username
                })
            });
            const data = await response.json();

            if (data.message) {
                alert(data.message);
                await loadAdminDashboard(); // Refresh the dashboard
            } else if (data.error) {
                alert(data.error);
            }
        } catch (error) {
            console.error('Error approving admin:', error);
            alert('An error occurred while approving the admin.');
        }
    }
}


// ================= USER DASHBOARD LOGIC =================

async function loadUserDashboard() {
    const items = await getItems();
    renderUserItemGrid(items);
}

function renderUserItemGrid(itemsList) {
    const grid = document.getElementById('items-grid');
    grid.innerHTML = '';
    if(itemsList.length === 0) {
        grid.innerHTML = '<p>No items reported yet.</p>';
        return;
    }
    itemsList.forEach(item => {
        const card = document.createElement('div');
        card.className = 'item-card';
        // Note: Real app would handle "Returned" status differently.
        // For now, we just show Lost/Found.
        const claimButton = item.status === 'Found' ? `<button class="btn-claim" onclick="claimItem(${item.id}, '${item.name.replace(/'/g, "\\'")}')">Claim Item</button>` : '';
        card.innerHTML = `
            <img src="${item.image}" class="item-card-img">
            <div class="item-card-body">
                <span class="status-badge status-${item.status}">${item.status}</span>
                <h3>${item.name}</h3>
                <p>${item.desc}</p>
                <div class="item-reporter">Reported by: ${item.reportedBy} on ${item.dateReported}</div>
                ${claimButton}
            </div>
        `;
        grid.appendChild(card);
    });
}

// Image Preview handler
document.getElementById('item-image-upload').addEventListener('change', function(event) {
    const preview = document.getElementById('image-preview');
    const file = event.target.files[0];
    if (file) {
        // In a real app, upload to server. Here we use FileReader for local preview.
        const reader = new FileReader();
        reader.onload = function(e) {
            preview.src = e.target.result;
            preview.classList.remove('hidden');
        }
        reader.readAsDataURL(file);
    }
});


// Report Item Form Submit
document.getElementById('report-form').addEventListener('submit', async function(e) {
    e.preventDefault();

    if (!currentUser) {
        alert('You must be logged in to report an item.');
        return;
    }

    const formData = new FormData();
    formData.append('status', document.getElementById('item-status').value);
    formData.append('name', document.getElementById('item-name').value);
    formData.append('desc', document.getElementById('item-desc').value);
    formData.append('reportedBy', currentUser.username);

    // Handle image upload
    const imageFile = document.getElementById('item-image-upload').files[0];
    if (imageFile) {
        formData.append('image', imageFile);
    }

    try {
        const response = await fetch(`${API_BASE}items`, {
            method: 'POST',
            credentials: 'include',
            body: formData
        });
        const data = await response.json();

        if (data.message) {
            alert(data.message);
            this.reset();
            document.getElementById('image-preview').classList.add('hidden');
            document.getElementById('image-preview').src = '#';

            // Switch back to view tab and reload grid
            switchUserTab('user-view-items');
            await loadUserDashboard();
        } else if (data.error) {
            alert(data.error);
        }
    } catch (error) {
        console.error('Error reporting item:', error);
        alert('An error occurred while reporting the item. Please try again.');
    }
});

async function userSearchItems() {
    const query = document.getElementById('user-search').value.toLowerCase();
    const allItems = await getItems();
    const filtered = allItems.filter(item =>
        item.name.toLowerCase().includes(query) ||
        item.desc.toLowerCase().includes(query)
    );
    renderUserItemGrid(filtered);
}

// ================= PROFILE MANAGEMENT =================

// Load user profile data when profile tab is opened
function loadUserProfile() {
    if (!currentUser) return;

    document.getElementById('profile-fname').value = currentUser.fname || '';
    document.getElementById('profile-mname').value = currentUser.mname || '';
    document.getElementById('profile-lname').value = currentUser.lname || '';
    document.getElementById('profile-email').value = currentUser.email || '';
    document.getElementById('profile-username').value = currentUser.username || '';
    document.getElementById('profile-password').value = ''; // Leave blank for security
}

// Profile form submission
document.getElementById('profile-form').addEventListener('submit', async function(e) {
    e.preventDefault();

    const updatedUser = {
        fname: document.getElementById('profile-fname').value,
        mname: document.getElementById('profile-mname').value,
        lname: document.getElementById('profile-lname').value,
        email: document.getElementById('profile-email').value,
        username: document.getElementById('profile-username').value
    };

    const newPassword = document.getElementById('profile-password').value;
    if (newPassword) {
        updatedUser.password = newPassword;
    }

    try {
        const response = await fetch(`${API_BASE}users?username=${currentUser.username}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify(updatedUser)
        });
        const data = await response.json();

        if (data.message) {
            // Update current user
            currentUser = { ...currentUser, ...updatedUser };

            // Update welcome message
            document.getElementById('welcome-user-msg').textContent = `Welcome, ${currentUser.fname} (${currentUser.type})`;

            alert('Profile updated successfully!');
            this.reset();
            loadUserProfile(); // Reload the form with updated data
        } else if (data.error) {
            alert(data.error);
        }
    } catch (error) {
        console.error('Error updating profile:', error);
        alert('An error occurred while updating the profile. Please try again.');
    }
});

// ================= ADMIN USER INFORMATION =================

// Load user information cards for admin
async function loadUserInformation() {
    const users = await getUsers();
    const grid = document.getElementById('user-info-grid');
    grid.innerHTML = '';

    users.forEach(user => {
        const card = document.createElement('div');
        card.className = 'user-info-card';
        card.innerHTML = `
            <h3><i class="fas fa-user-circle"></i> ${user.fname} ${user.mname} ${user.lname}</h3>
            <p><strong>Username:</strong> ${user.username}</p>
            <p><strong>Email:</strong> ${user.email}</p>
            <p><strong>Type:</strong> <span class="user-type">${user.type}</span></p>
            <p><strong>Full Name:</strong> ${user.fname} ${user.mname ? user.mname + ' ' : ''}${user.lname}</p>
        `;
        grid.appendChild(card);
    });
}

// Modify switchAdminTab to load user info when that tab is selected
function switchAdminTab(tabId) {
    document.querySelectorAll('.admin-tab-content').forEach(el => el.classList.add('hidden'));
    document.getElementById(tabId).classList.remove('hidden');
    document.querySelectorAll('#admin-dash-view .side-btn').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');

    // Load data for specific tabs
    if (tabId === 'admin-user-info') {
        loadUserInformation();
    }
}

// ================= CLAIMS FUNCTIONALITY =================

// Claim an item
async function claimItem(itemId, itemName) {
    const claimDescription = prompt(`Please describe why you believe "${itemName}" is yours:`);
    if (!claimDescription || claimDescription.trim() === '') {
        alert('Claim description is required.');
        return;
    }

    try {
        const response = await fetch(`${API_BASE}claims`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            credentials: 'include',
            body: JSON.stringify({
                action: 'create',
                item_id: itemId,
                claimant_username: currentUser.username,
                claim_description: claimDescription.trim()
            })
        });
        const data = await response.json();

        if (data.message) {
            alert(data.message);
            await loadUserDashboard(); // Refresh the dashboard
        } else if (data.error) {
            alert(data.error);
        }
    } catch (error) {
        console.error('Error claiming item:', error);
        alert('An error occurred while claiming the item. Please try again.');
    }
}

// Load user claims
async function loadUserClaims() {
    try {
        const response = await fetch(`${API_BASE}claims?action=user&username=${currentUser.username}`, { credentials: 'include' });
        const data = await response.json();
        renderUserClaimsTable(data.claims || []);
    } catch (error) {
        console.error('Error fetching user claims:', error);
        renderUserClaimsTable([]);
    }
}

// Render user claims table
function renderUserClaimsTable(claimsList) {
    const tbody = document.getElementById('user-claims-table-body');
    tbody.innerHTML = '';
    if (claimsList.length === 0) {
        tbody.innerHTML = '<tr><td colspan="4">No claims submitted yet.</td></tr>';
        return;
    }
    claimsList.forEach(claim => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${claim.item_name}</td>
            <td>${claim.claim_description}</td>
            <td><span class="status-badge status-${claim.status}">${claim.status}</span></td>
            <td>${claim.date_submitted}</td>
        `;
        tbody.appendChild(tr);
    });
}

// Load admin claims
async function loadAdminClaims() {
    try {
        const response = await fetch(`${API_BASE}claims?action=pending`, { credentials: 'include' });
        const data = await response.json();
        renderAdminClaimsTable(data.claims || []);
    } catch (error) {
        console.error('Error fetching admin claims:', error);
        renderAdminClaimsTable([]);
    }
}

// Render admin claims table
function renderAdminClaimsTable(claimsList) {
    const tbody = document.getElementById('claims-table-body');
    tbody.innerHTML = '';
    if (claimsList.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6">No pending claims.</td></tr>';
        return;
    }
    claimsList.forEach(claim => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${claim.item_name}</td>
            <td>${claim.claimant_name}</td>
            <td>${claim.claim_description}</td>
            <td><span class="status-badge status-${claim.status}">${claim.status}</span></td>
            <td>${claim.date_submitted}</td>
            <td>
                <button class="btn-approve" onclick="approveClaim(${claim.id})">Approve</button>
                <button class="btn-delete" onclick="rejectClaim(${claim.id})">Reject</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// Approve claim
async function approveClaim(claimId) {
    if (confirm('Are you sure you want to approve this claim?')) {
        try {
            const response = await fetch(`${API_BASE}claims`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify({
                    action: 'approve',
                    id: claimId,
                    approver_username: currentUser.username
                })
            });
            const data = await response.json();

            if (data.message) {
                alert(data.message);
                await loadAdminClaims(); // Refresh the claims
            } else if (data.error) {
                alert(data.error);
            }
        } catch (error) {
            console.error('Error approving claim:', error);
            alert('An error occurred while approving the claim.');
        }
    }
}

// Reject claim
async function rejectClaim(claimId) {
    if (confirm('Are you sure you want to reject this claim?')) {
        try {
            const response = await fetch(`${API_BASE}claims`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'include',
                body: JSON.stringify({
                    action: 'reject',
                    id: claimId,
                    approver_username: currentUser.username
                })
            });
            const data = await response.json();

            if (data.message) {
                alert(data.message);
                await loadAdminClaims(); // Refresh the claims
            } else if (data.error) {
                alert(data.error);
            }
        } catch (error) {
            console.error('Error rejecting claim:', error);
            alert('An error occurred while rejecting the claim.');
        }
    }
}

// Modify switchAdminTab to load claims when that tab is selected
function switchAdminTab(tabId) {
    document.querySelectorAll('.admin-tab-content').forEach(el => el.classList.add('hidden'));
    document.getElementById(tabId).classList.remove('hidden');
    document.querySelectorAll('#admin-dash-view .side-btn').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');

    // Load data for specific tabs
    if (tabId === 'admin-user-info') {
        loadUserInformation();
    } else if (tabId === 'admin-claims') {
        loadAdminClaims();
    }
}

// Modify switchUserTab to load profile data and claims when respective tabs are selected
function switchUserTab(tabId) {
    document.querySelectorAll('.user-tab-content').forEach(el => el.classList.add('hidden'));
    document.getElementById(tabId).classList.remove('hidden');
    document.querySelectorAll('#user-dash-view .side-btn').forEach(btn => btn.classList.remove('active'));
    event.target.classList.add('active');

    // Load data for specific tabs
    if (tabId === 'user-profile') {
        loadUserProfile();
    } else if (tabId === 'user-my-claims') {
        loadUserClaims();
    }
}
