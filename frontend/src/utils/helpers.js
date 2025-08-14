export function formatDate(dateString) {
    const date = new Date(dateString)
    return date.toLocaleDateString()
}

export function validateEmail(email) {
    return /\S+@\S+\.\S+/.test(email)
}
