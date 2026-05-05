const colorMap = {
  // Rental statuses
  PENDING: 'badge-warning',
  CONFIRMED: 'badge-info',
  ACTIVE: 'badge-success',
  COMPLETED: 'badge-secondary',
  CANCELLED: 'badge-danger',
  // Car statuses
  AVAILABLE: 'badge-success',
  RENTED: 'badge-info',
  MAINTENANCE: 'badge-warning',
  // License statuses
  APPROVED: 'badge-success',
  REJECTED: 'badge-danger',
};

export default function StatusBadge({ status }) {
  const cls = colorMap[status] || 'badge-secondary';
  return <span className={`badge ${cls}`}>{status}</span>;
}
