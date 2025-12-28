# UEP Lost and Found System

A comprehensive web-based Lost and Found management system built for University of the Eastern Philippines (UEP). This system allows students, faculty, and staff to report lost or found items, make claims, and manage the entire process through an intuitive web interface.

## Features

### User Management
- **User Registration & Authentication**
  - Support for Student, Faculty, UEP Staff, and Admin user types
  - Secure password hashing with BCrypt
  - Profile management and editing

### Item Management
- **Report Lost/Found Items**
  - Upload images for items
  - Detailed descriptions and location information
  - Categorization by status (Lost/Found)

### Claim System
- **Item Claims**
  - Users can claim found items
  - Admin approval workflow for claims
  - Status tracking (Pending, Approved, Rejected)

### Admin Dashboard
- **Comprehensive Management**
  - Manage all users and their accounts
  - Approve/reject admin requests
  - Moderate item reports and claims
  - View system-wide statistics and information

## System Architecture

### Backend
- **Framework**: Spring Boot 3.1.5
- **Language**: Java 17
- **Database**: MySQL (with H2 fallback support)
- **Security**: Spring Security
- **ORM**: Spring Data JPA
- **Build Tool**: Gradle

### Frontend
- **Technology**: HTML5, CSS3, JavaScript (ES6+)
- **Styling**: Custom CSS with Font Awesome icons
- **Fonts**: Google Fonts (Noto Serif, Poppins)
- **Type**: Single Page Application (SPA)

### Database Schema
- **users**: User accounts and profiles
- **items**: Lost/found item reports
- **claims**: Item claim requests and status

## Prerequisites

Before running the system, ensure you have the following installed:

1. **Java Development Kit (JDK) 17 or higher**
   - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or use OpenJDK
   - Verify installation: `java -version`

2. **MySQL Server 8.0 or higher**
   - Download from [MySQL Official Site](https://dev.mysql.com/downloads/mysql/)
   - Default port: 3306
   - Default credentials: username=`root`, password=`neil2003`

3. **Git** (optional, for version control)
   - Download from [Git Official Site](https://git-scm.com/)

## Installation & Setup

### 1. Database Setup

#### Option A: Using MySQL Command Line
```sql
-- Login to MySQL
mysql -u root -p

-- Create database (if not using the automatic schema creation)
CREATE DATABASE uep_lost_and_found_ws;
EXIT;
```

#### Option B: Using MySQL Workbench
1. Open MySQL Workbench
2. Connect to your MySQL server
3. Execute the schema.sql file located in `src/main/resources/schema.sql`

### 2. System Setup

1. **Clone or download the project**
   ```bash
   # If using Git
   git clone <repository-url>
   cd UEP_Lost_and_Found_WS
   ```

2. **Configure Database Connection**
   - Edit `src/main/resources/application.properties`
   - Update database credentials if different from defaults:
     ```properties
     spring.datasource.username=your_username
     spring.datasource.password=your_password
     ```

## Running the System

### Quick Start (Recommended)

Use the provided batch script for easy startup:

```bash
# Windows
start_system.bat

# Or run individually
run_backend.bat    # Starts only the backend
open_frontend.bat  # Opens frontend in browser
```

### Manual Startup

#### Step 1: Start the Backend Server
```bash
# From the project root directory
./gradlew bootRun

# On Windows
gradlew.bat bootRun
```

**What happens:**
- Database schema is automatically created
- Default admin user is inserted
- Spring Boot server starts on http://localhost:8080
- Server will be ready in 30-60 seconds

#### Step 2: Open the Frontend
- Open your web browser
- Navigate to: `file:///path/to/project/src/main/resources/static/index.html`
- Or use the `open_frontend.bat` script

## Default Login Credentials

### Administrator Account
- **Username**: `admin`
- **Password**: `uep123`
- **Role**: System Administrator
- **Permissions**: Full system access

### Creating New Accounts
1. Use the signup form on the frontend
2. Select your user type (Student, Faculty, UEP Staff)
3. Optionally request admin privileges
4. Wait for admin approval (for admin requests)

## Project Structure

```
UEP_Lost_and_Found_WS/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── Backend/
│   │   │       ├── UepLostAndFoundApplication.java
│   │   │       ├── config/          # Configuration classes
│   │   │       ├── controller/      # REST controllers
│   │   │       ├── model/           # Entity models
│   │   │       ├── repository/      # Data repositories
│   │   │       ├── service/         # Business logic
│   │   │       └── util/            # Utility classes
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── schema.sql           # Database schema
│   │       └── static/              # Frontend files
│   │           ├── index.html
│   │           ├── style.css
│   │           ├── script.js
│   │           └── LARAWAN/         # Image assets
│   └── test/                        # Test files
├── build.gradle                     # Gradle build configuration
├── gradlew                          # Gradle wrapper (Unix)
├── gradlew.bat                      # Gradle wrapper (Windows)
├── run_backend.bat                  # Backend startup script
├── open_frontend.bat                # Frontend opening script
├── start_system.bat                 # Complete system startup
└── README.md                        # This file
```

## Configuration

### Database Configuration
Edit `src/main/resources/application.properties`:

```properties
# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/uep_lost_and_found_ws?charset=utf8mb4&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=neil2003

# Server Configuration
server.port=8080

# Session Configuration
server.servlet.session.timeout=30m
```

### Changing Default Admin Password
1. Start the application once to create the default admin
2. Login with default credentials
3. Go to Profile section and change password
4. Or modify the BCrypt hash in `schema.sql` before first run

## Testing

### Running Backend Tests
```bash
./gradlew test
```

### Manual Testing Checklist
- [ ] User registration works
- [ ] Login/logout functionality
- [ ] Item reporting and image upload
- [ ] Claim submission and approval
- [ ] Admin user management
- [ ] Profile editing
- [ ] Search functionality

## Security Features

- **Password Security**: BCrypt hashing
- **Session Management**: Configurable session timeout
- **Input Validation**: Server-side validation for all inputs
- **SQL Injection Protection**: Using Prepared Statements via JPA
- **XSS Protection**: Input sanitization in frontend

## Troubleshooting

### Common Issues

#### 1. Database Connection Error
**Problem**: Cannot connect to MySQL database
**Solution**:
- Ensure MySQL service is running
- Check credentials in `application.properties`
- Verify database exists: `uep_lost_and_found_ws`

#### 2. Port 8080 Already in Use
**Problem**: Spring Boot fails to start
**Solution**:
```bash
# Find process using port 8080
netstat -ano | findstr :8080

# Kill the process (replace PID with actual process ID)
taskkill /PID <PID> /F
```

#### 3. Gradle Build Failed
**Problem**: Dependencies cannot be downloaded
**Solution**:
```bash
# Clean and rebuild
./gradlew clean build

# Force refresh dependencies
./gradlew --refresh-dependencies bootRun
```

#### 4. Frontend Not Loading
**Problem**: Blank page or 404 errors
**Solution**:
- Ensure frontend files exist in `src/main/resources/static/`
- Check browser console for JavaScript errors
- Verify file permissions

#### 5. Image Upload Not Working
**Problem**: Cannot upload item images
**Solution**:
- Check file size limits
- Ensure supported image formats (JPG, PNG, GIF)
- Verify directory permissions

### Log Files
- **Backend Logs**: Check console output during `bootRun`
- **Database Logs**: Check MySQL error logs
- **Browser Console**: F12 → Console tab for frontend errors

## System Requirements

### Minimum Requirements
- **OS**: Windows 10/11, macOS 10.15+, Linux (Ubuntu 18.04+)
- **RAM**: 4GB
- **Storage**: 2GB free space
- **Java**: JDK 17+
- **Database**: MySQL 8.0+

### Recommended Requirements
- **OS**: Windows 11, macOS 12+, Linux (Ubuntu 20.04+)
- **RAM**: 8GB
- **Storage**: 5GB free space
- **Java**: JDK 21
- **Database**: MySQL 8.0+ with dedicated instance

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature-name`
3. Make your changes
4. Test thoroughly
5. Commit: `git commit -m "Add feature description"`
6. Push: `git push origin feature-name`
7. Create a Pull Request

## License

This project is developed for University of the Eastern Philippines (UEP) internal use.

## Support

For technical support or questions:
- Check the troubleshooting section above
- Review the application logs
- Contact the system administrator

## Updates & Maintenance

### Regular Maintenance Tasks
1. **Database Backup**: Regularly backup the MySQL database
2. **Log Rotation**: Monitor and rotate application logs
3. **Security Updates**: Keep Java and dependencies updated
4. **User Management**: Review and clean up inactive user accounts

### Updating the System
1. Backup current database
2. Download/merge latest code changes
3. Run database migrations if provided
4. Test thoroughly in development environment
5. Deploy to production

---

**Version**: 1.0.0  
**Last Updated**: 2024  
**Compatibility**: Spring Boot 3.1.5, Java 17+

### Group Members
1. Neil Boy C. France
2. Kimberly Ann Faye F. Dugan
3. Reny Jr. F. Delos Reyes
4. Kharylle M. Uy
5. Belleza Lou C. Ybanez