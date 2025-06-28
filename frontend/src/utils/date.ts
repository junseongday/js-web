import dayjs from 'dayjs'

export function parseDate(arr: number[] | undefined | null): Date | null {
  if (!Array.isArray(arr) || arr.length < 6) return null;
  // arr: [year, month, day, hour, minute, second, nano]
  return new Date(
    arr[0],
    arr[1] - 1,
    arr[2],
    arr[3],
    arr[4],
    arr[5],
    Math.floor((arr[6] || 0) / 1000000)
  );
}

export function formatDate(arr: number[] | undefined | null, format = 'YYYY.MM.DD HH:mm:ss'): string {
  const date = parseDate(arr);
  if (!date || isNaN(date.getTime())) return '-';
  return dayjs(date).format(format);
} 