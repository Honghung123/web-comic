import React, { useState } from 'react';
import axios from 'axios';

const comicTitle = 'Chuong 04: Thiên địa dị tải, luyện thể thiên phú?';
const comicContent = `
<p>Hàng Châu tam trung trên bãi tập, các học sinh từ ríu.<br> <br> Ngay tại tổ chức, 
là Lam Tinh một người đị một tâm tuồi liên muốn tiến hành thức thị.<br>
`;

export default function ComicInfo() {
    const handleSubmit = async (event) => {
        event.preventDefault();
        const payload = {
            title: comicTitle,
            content: comicContent,
        };
        const url = `http://localhost:8080/api/v1/comic/export-file?converter_id=1`;
        try {
            const response = await axios.post(url, payload, {
                responseType: 'blob',
            });
            console.log(response);
            const blob = new Blob([response.data], { type: 'application/epub+zip' });
            const windowUrl = window.URL || window.webkitURL;
            const downloadUrl = windowUrl.createObjectURL(blob);
            const anchor = document.createElement('a');
            anchor.href = downloadUrl;
            anchor.download = comicTitle;
            document.body.appendChild(anchor);
            anchor.click();
            // Xóa URL sau khi đã tải xuống
            window.URL.revokeObjectURL(downloadUrl);
        } catch (error) {
            alert('Chức năng này chưa làm nha Fen');
        }
    };
    return (
        <>
            <h1>This is the information of a Novel</h1>

            <br />
        </>
    );
}
