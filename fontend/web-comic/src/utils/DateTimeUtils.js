
export const getDiffTime = (dateString) => {

    const givenDate = new Date(dateString);
    if (isNaN(givenDate.getTime())) {
        return dateString;
    }
    const currentDate = new Date();
    const diffInMilliseconds = currentDate - givenDate;
    const diffInMinutes = diffInMilliseconds / (1000 * 60);

    let result;
    if (diffInMinutes <= 0) {
        result = '1 phút trước';
    }
    else if (diffInMinutes > 1440) {
        // 1440 minute = 24 hour
        const diffInDays = diffInMinutes / 1440;
        result = `${Math.floor(diffInDays)} ngày trước`;
    } else if (diffInMinutes > 60) {
        const diffInHours = diffInMinutes / 60;
        result = `${Math.floor(diffInHours)} giờ trước`;
    } else {
        result = `${Math.floor(diffInMinutes)} phút trước`;
    }

    return result;
}