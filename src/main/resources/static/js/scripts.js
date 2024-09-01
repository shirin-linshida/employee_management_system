document.addEventListener('DOMContentLoaded', function() {
    const apiUrl = 'http://localhost:8080/api';
    const token = localStorage.getItem('jwtToken');

    // Handle login
    document.getElementById('loginForm')?.addEventListener('submit', function(event) {
        event.preventDefault();
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        fetch(`${apiUrl}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        })
            .then(response => response.json())
            .then(data => {
                if (data) {
                    console.log("data available",data.token);
                    localStorage.setItem('jwtToken', data);

                    // Fetch user details to determine role
                    fetch(`${apiUrl}/user/details`, {
                        headers: {
                            'Authorization': `Bearer ${data}`
                        }
                    })
                        .then(response => response.json())
                        .then(userData => {
                            if (userData.roles.some(role => role.authority === 'ROLE_ADMIN')) {
                                window.location.href = 'admin-dashboard.html';
                            } else if (userData.roles.some(role => role.authority === 'ROLE_STAFF')) {
                                window.location.href = 'staff-dashboard.html';
                            } else {
                                console.log("userrorle",userData);
                                alert('Unauthorized role');
                                localStorage.removeItem('jwtToken');
                            }
                        })
                        .catch(error => {
                            console.error('Login error:', error);
                            document.getElementById('error-message').textContent = 'Login request failed. Please try again.';
                            document.getElementById('error-message').style.display = 'block';
                        });

                } else {
                    console.log("data not available",data);
                    alert('Login failed');
                }
            })
            .catch(error => {
                console.error('Login error:', error);
                alert('Login request failed');
            });
    });

    // Handle logout
    document.getElementById('logoutButton')?.addEventListener('click', function() {
        localStorage.removeItem('jwtToken');
        window.location.href = 'login.html';
    });

    // Fetch employee details for admin
    if (document.getElementById('employeeTable')) {
        fetch(`${apiUrl}/admin/employees`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
            .then(response => response.json())
            .then(data => {
                const tableBody = document.querySelector('#employeeTable tbody');
                tableBody.innerHTML = '';
                data.forEach(employee => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                    <td>${employee.id}</td>
                    <td>${employee.username}</td>
                    <td>${employee.role}</td>
                    <td>
                        <button onclick="editEmployee(${employee.id})">Edit</button>
                        <button onclick="deleteEmployee(${employee.id})">Delete</button>
                    </td>
                `;
                    tableBody.appendChild(row);
                });
            })
            .catch(error => {
                console.error('Fetch employees error:', error);
                alert('Failed to fetch employee details');
            });
    }

    // Handle profile update for staff
    document.getElementById('updateProfileForm')?.addEventListener('submit', function(event) {
        event.preventDefault();
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;

        fetch(`${apiUrl}/staff/profile`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        })
            .then(response => response.json())
            .then(data => alert('Profile updated'))
            .catch(error => {
                console.error('Update profile error:', error);
                alert('Failed to update profile');
            });
    });

    function editEmployee(id) {
        // Implement editing functionality
    }

    function deleteEmployee(id) {
        fetch(`${apiUrl}/admin/employees/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
            .then(response => response.json())
            .then(() => location.reload())
            .catch(error => {
                console.error('Delete employee error:', error);
                alert('Failed to delete employee');
            });
    }
});
