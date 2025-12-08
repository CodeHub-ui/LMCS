// Modal functionality
function confirmDownload(url) {
    const modal = document.getElementById('downloadModal');
    const proceedBtn = document.querySelector('.modal-buttons .btn:first-child');

    modal.style.display = 'flex';
    proceedBtn.onclick = () => proceedDownload(url);
}

function proceedDownload(url) {
    window.open(url, '_blank');
    closeModal();
}

function closeModal() {
    const modal = document.getElementById('downloadModal');
    modal.style.display = 'none';
}

// Close modal when clicking outside
window.onclick = function(event) {
    const modal = document.getElementById('downloadModal');
    if (event.target === modal) {
        closeModal();
    }
}

// Form handling
document.addEventListener('DOMContentLoaded', function() {
    const contactForm = document.getElementById('contactForm');
    const successMessage = document.getElementById('successMessage');

    if (contactForm) {
        contactForm.addEventListener('submit', function(e) {
            e.preventDefault();

            // Get form data
            const formData = new FormData(contactForm);
            const name = formData.get('name');
            const email = formData.get('email');
            const message = formData.get('message');

            // Basic validation
            if (!name || !email || !message) {
                alert('Please fill in all fields.');
                return;
            }

            // Email validation
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email)) {
                alert('Please enter a valid email address.');
                return;
            }

            // Show success message
            successMessage.style.display = 'block';

            // Reset form
            contactForm.reset();

            // Hide success message after 5 seconds
            setTimeout(() => {
                successMessage.style.display = 'none';
            }, 5000);
        });
    }
});

// Email and WhatsApp functions
function sendEmail() {
    const email = 'autolib.69@gmail.com';
    const subject = 'Inquiry from Library Management System Website';
    const body = 'Hello,\n\nI have a question about the Automatic Library Management Checkout System.\n\nBest regards,';

    const mailtoLink = `mailto:${email}?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`;
    window.location.href = mailtoLink;
}

function sendWhatsApp() {
    const phone = '+918273719318';
    const message = 'Hello, I have a question about the Automatic Library Management Checkout System.';

    const whatsappLink = `https://wa.me/${phone.replace(/\+/g, '')}?text=${encodeURIComponent(message)}`;
    window.open(whatsappLink, '_blank');
}

// Button ripple effect
document.addEventListener('DOMContentLoaded', function() {
    const buttons = document.querySelectorAll('.btn');

    buttons.forEach(button => {
        button.addEventListener('click', function(e) {
            const ripple = document.createElement('span');
            ripple.classList.add('ripple');

            const rect = button.getBoundingClientRect();
            const size = Math.max(rect.width, rect.height);
            const x = e.clientX - rect.left - size / 2;
            const y = e.clientY - rect.top - size / 2;

            ripple.style.width = ripple.style.height = size + 'px';
            ripple.style.left = x + 'px';
            ripple.style.top = y + 'px';

            button.appendChild(ripple);

            setTimeout(() => {
                ripple.remove();
            }, 600);
        });
    });
});

// Function to open the admin portal PDF
function openPDF() {
    window.open('file:///D:/LMS%20PROJECT/WebSites%20LMS/admin%20pic.pdf', '_blank');
}

// Function to open the user portal PDF
function openUserPDF() {
    window.open('file:///D:/LMS%20PROJECT/WebSites%20LMS/user%20pic.pdf', '_blank');
}
